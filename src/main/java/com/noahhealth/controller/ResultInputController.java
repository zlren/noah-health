package com.noahhealth.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.noahhealth.bean.CommonResult;
import com.noahhealth.bean.Constant;
import com.noahhealth.bean.Identity;
import com.noahhealth.bean.PageResult;
import com.noahhealth.bean.input.ResultInputDetailExtend;
import com.noahhealth.bean.input.ResultInputExtend;
import com.noahhealth.bean.rolecheck.RequiredRoles;
import com.noahhealth.bean.user.UserExtend;
import com.noahhealth.pojo.*;
import com.noahhealth.service.*;
import com.noahhealth.util.TimeUtil;
import com.noahhealth.util.Validator;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 化验、医技数据管理
 * Created by zlren on 2017/6/21.
 */
@RequestMapping("input")
@RestController
@Slf4j
public class ResultInputController {

    @Autowired
    private UserService userService;

    @Autowired
    private ResultInputService resultInputService;

    @Autowired
    private ResultInputDetailService resultInputDetailService;

    @Autowired
    private CategorySecondService categorySecondService;

    @Autowired
    private CategoryThirdService categoryThirdService;

    @Autowired
    private PropertyService propertyService;


    /**
     * 根据inputId查询单条result的详情
     *
     * @param inputId
     * @return
     */
    @RequestMapping(value = "{inputId}", method = RequestMethod.GET)
    public CommonResult queryResultInputDetailByInputId(@PathVariable("inputId") Integer inputId) {

        ResultInput resultInput = this.resultInputService.queryById(inputId);
        ResultInputExtend resultInputExtend = this.resultInputService.extendFromResultInput(resultInput);

        ResultInputDetail resultInputDetailRecord = new ResultInputDetail();
        resultInputDetailRecord.setResultInputId(resultInputExtend.getId());
        List<ResultInputDetail> resultInputDetailList = this.resultInputDetailService.queryListByWhere
                (resultInputDetailRecord);

        resultInputExtend.data = this.resultInputDetailService.extendFromResultInputDetailList
                (resultInputDetailList);

        return CommonResult.success("查询成功", resultInputExtend);
    }


    /**
     * 在input表增加一条记录
     *
     * @param params
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public CommonResult addResultInput(@RequestBody Map<String, Object> params, HttpSession session) {

        Integer userId = (Integer) params.get("userId");

        {
            User record = new User();
            record.setId(userId);
            if (this.userService.queryOne(record).getRole().equals(Constant.USER_1)) {
                // 1级用户没有此权限
                return CommonResult.failure("一级用户无此权限");
            }
        }

        Integer secondId = (Integer) params.get("secondId");
        Integer inputerId = Integer.valueOf(((Identity) session.getAttribute(Constant.IDENTITY)).getId());
        String status = Constant.LU_RU_ZHONG; // 初始状态为录入中
        String note = (String) params.get(Constant.NOTE);
        String hospital = (String) params.get("hospital");
        Date time = TimeUtil.parseTime((String) params.get(Constant.TIME));

        ResultInput resultInput = new ResultInput();
        resultInput.setUserId(userId);
        resultInput.setSecondId(secondId);
        resultInput.setInputerId(inputerId);
        resultInput.setStatus(status);
        resultInput.setNote(note);
        resultInput.setHospital(hospital);
        resultInput.setTime(time);
        resultInput.setUploadTime(TimeUtil.getCurrentTime());

        // 级联插入
        this.resultInputService.saveInputAndEmptyDetail(resultInput);

        return CommonResult.success("添加成功");
    }


    /**
     * 删除input记录，级联删除detail表
     *
     * @param inputId
     * @return
     */
    @RequestMapping(value = "{inputId}", method = RequestMethod.DELETE)
    @RequiredRoles(roles = {"系统管理员", "档案部员工", "档案部主管"})
    public CommonResult deleteResultInputById(@PathVariable("inputId") Integer inputId, HttpSession session) {

        Identity identity = (Identity) session.getAttribute(Constant.IDENTITY);
        String identityRole = identity.getRole();
        String identityId = identity.getId();

        ResultInput resultInput = this.resultInputService.queryById(inputId);

        // if (this.userService.checkArchiver(identityRole)) {
        //     // 如果是档案部员工，这条记录必须是他创建的
        //     if (!resultInput.getInputerId().equals(Integer.valueOf(identityId))) {
        //         return CommonResult.failure("无此权限");
        //     }
        // } else if (this.userService.checkArchiverManager(identityRole)) {
        //     // 如果是档案部主管，这条记录必须是手下的人创建的
        //     Set<Integer> archiverIdSet = this.userService.queryStaffIdSetUnderManager(identity);
        //
        //     if (!archiverIdSet.contains(resultInput.getInputerId())) { // 再加上或是自己创建的
        //         return CommonResult.failure("无此权限");
        //     }
        // }

        boolean result = this.resultInputService.deleteInput(inputId);
        if (!result) {
            return CommonResult.failure("删除失败");
        }

        return CommonResult.success("删除成功");
    }


    /**
     * 本质就是条件查询user表的会员
     *
     * @param params
     * @param session
     * @return
     */
    @RequestMapping(value = "list", method = RequestMethod.POST)
    public CommonResult queryResultInputUserList(@RequestBody Map<String, Object> params, HttpSession session) {

        Integer pageNow = (Integer) params.get(Constant.PAGE_NOW);
        Integer pageSize = (Integer) params.get(Constant.PAGE_SIZE);
        String userName = (String) params.get("userName");
        String memberNum = (String) params.get("memberNum");
        Identity identity = (Identity) session.getAttribute(Constant.IDENTITY);

        // 过期的用户看不了
        if (!this.userService.checkValid(identity.getId())) {
            return CommonResult.failure("过期无效的用户");
        }

        List<User> userList = this.resultInputService.queryResultInputUserList(identity, userName, memberNum,
                pageNow, pageSize);
        PageResult pageResult = new PageResult(new PageInfo<>(userList));

        List<UserExtend> userExtendList = this.userService.extendFromUser(userList);
        pageResult.setData(userExtendList);

        return CommonResult.success("查询成功", pageResult);
    }


    /**
     * 档案部查的时候，直接查那些uploaderId是自己的所有记录，不要再嵌套一层user列表
     * ADMIN也调用的这个
     *
     * @return
     */
    @RequestMapping(value = "list_by_arc", method = RequestMethod.POST)
    public CommonResult queryResultInputListByArc(@RequestBody Map<String, Object> params, HttpSession session) {

        Integer pageNow = (Integer) params.get(Constant.PAGE_NOW);
        Integer pageSize = (Integer) params.get(Constant.PAGE_SIZE);
        String userName = (String) params.get("userName");
        String memberNum = (String) params.get("memberNum");
        String inputerName = (String) params.get("inputerName");
        String checkerName = (String) params.get("checkerName");
        String type = (String) params.get("type"); // 化验或者医技
        Date beginTime = TimeUtil.parseTime((String) params.get("beginTime"));
        Date endTime = TimeUtil.parseTime((String) params.get("endTime"));
        String status = (String) params.get(Constant.STATUS);
        Identity identity = (Identity) session.getAttribute(Constant.IDENTITY);

        List<ResultInput> resultInputList = this.resultInputService.queryInputListByArc(pageNow, pageSize, userName,
                memberNum, beginTime, endTime, status, identity, inputerName, checkerName, type);
        PageResult pageResult = new PageResult(new PageInfo<>(resultInputList));

        log.info("查询的结果的条数：{}", pageResult.getRowCount());

        List<ResultInputExtend> resultInputExtendList = this.resultInputService.extendFromResultInputList
                (resultInputList);
        pageResult.setData(resultInputExtendList);

        return CommonResult.success("查询成功", pageResult);
    }


    /**
     * 根据userId查询单个member的所有检查结果
     *
     * @param userId
     * @return
     */
    @RequestMapping(value = "list/{userId}", method = RequestMethod.POST)
    public CommonResult queryResultAndDetailListByUserId(@PathVariable("userId") Integer userId, HttpSession session,
                                                         @RequestBody Map<String, Object> params) {

        Identity identity = (Identity) session.getAttribute(Constant.IDENTITY);
        String type = (String) params.get("type");
        String status = (String) params.get(Constant.STATUS);
        String normal = (String) params.get("normal");
        Integer secondId = (Integer) params.get(Constant.SECOND_ID);
        Date beginTime = TimeUtil.parseTime((String) params.get("beginTime"));
        Date endTime = TimeUtil.parseTime((String) params.get("endTime"));

        // resultInputList
        List<ResultInput> resultInputList = this.resultInputService.queryResultAndDetailListByUserId(identity,
                userId, type, status, secondId, beginTime, endTime);

        // 过滤异常
        log.info("normal的值是: {}", normal);
        if ("异常".equals(normal)) {
            resultInputList = resultInputList.stream().filter(
                    resultInput -> this.resultInputService.isError(resultInput.getId())
            ).collect(Collectors.toList());
        }

        // resultInputExtendList
        List<ResultInputExtend> resultInputExtendList = this.resultInputService.extendFromResultInputList
                (resultInputList);

        // resultInputExtendList with detail
        resultInputExtendList.forEach(resultInputExtend -> {

            // ResultInputDetail resultInputDetailRecord = new ResultInputDetail();
            // resultInputDetailRecord.setResultInputId(resultInputExtend.getId());

            Example example = new Example(ResultInputDetail.class);
            Example.Criteria criteria = example.createCriteria();


            criteria.andEqualTo("resultInputId", resultInputExtend.getId());

            // 不想看到那些没有值的检查项目
            // 用户自己看不到，顾问、顾问部主管看不到
            if (this.userService.checkMember(identity.getRole()) || this.userService.checkAdviser(identity.getRole())
                    || this.userService.checkAdviseManager(identity.getRole())) {
                criteria.andCondition("length(value)>", 0);
            }

            // List<ResultInputDetail> resultInputDetailList = this.resultInputDetailService.queryListByWhere
            //         (resultInputDetailRecord);

            List<ResultInputDetail> resultInputDetailList = this.resultInputDetailService.getMapper().selectByExample
                    (example);

            resultInputExtend.data = this.resultInputDetailService.extendFromResultInputDetailList
                    (resultInputDetailList);
        });

        return CommonResult.success("查询成功", resultInputExtendList);
    }


    /**
     * 根据userId查询单个member的所有检查结果
     *
     * @param userId
     * @return
     */
    @RequestMapping(value = "list_page/{userId}", method = RequestMethod.POST)
    public CommonResult queryResultAndDetailPageListByUserId(
            @PathVariable("userId") Integer userId,
            HttpSession session,
            @RequestBody Map<String, Object> params) {

        Identity identity = (Identity) session.getAttribute(Constant.IDENTITY);
        String type = (String) params.get("type");
        Integer secondId = (Integer) params.get(Constant.SECOND_ID);
        Integer pageNow = (Integer) params.get(Constant.PAGE_NOW);
        Integer pageSize = (Integer) params.get(Constant.PAGE_SIZE);

        Date beginTime = TimeUtil.parseTime((String) params.get("beginTime"));
        Date endTime = TimeUtil.parseTime((String) params.get("endTime"));

        // 如果指定亚类，那么就只查此亚类的所有检查记录
        // 没有指定亚类，就查type（医技或者化验）下面的所有亚类
        Set<Integer> secondIdSet;
        if (secondId == -1) {
            secondIdSet = this.categorySecondService.getSecondIdSetByFirstType(type);
        } else {
            secondIdSet = new HashSet<>();
            secondIdSet.add(secondId);
        }

        List<Map<String, Object>> result = new ArrayList<>();

        secondIdSet.forEach(sid -> {

            CategorySecond categorySecond = this.categorySecondService.queryById(sid);

            ResultInput resultInput = new ResultInput();
            resultInput.setUserId(userId);
            resultInput.setSecondId(sid);
            resultInput.setStatus(Constant.YI_TONG_GUO);

            Example example = new Example(ResultInput.class);
            Example.Criteria criteria = example.createCriteria();

            criteria.andEqualTo("userId", userId);
            criteria.andEqualTo("secondId", sid);
            criteria.andEqualTo("status", Constant.YI_TONG_GUO);
            example.setOrderByClause("time DESC"); // 倒叙

            // 时间
            if (beginTime != null && endTime != null) {
                criteria.andBetween("time", beginTime, endTime);
            }

            PageHelper.startPage(pageNow, pageSize);
            List<ResultInput> resultInputList = this.resultInputService.getMapper().selectByExample(example);
            PageInfo<ResultInput> pageInfo = new PageInfo<>(resultInputList);

            if (resultInputList.size() > 0) {

                Map<String, Object> m = new HashMap<>();

                m.put("secondId", categorySecond.getId());
                m.put("secondName", categorySecond.getName());
                m.put("colTotal", pageInfo.getTotal());

                List<ResultInputExtend> l = new ArrayList<>();
                resultInputList.forEach(data -> {

                    ResultInputDetail resultInputDetail = new ResultInputDetail();
                    resultInputDetail.setResultInputId(data.getId());
                    List<ResultInputDetail> resultInputDetailList = this.resultInputDetailService.queryListByWhere
                            (resultInputDetail);

                    ResultInputExtend resultInputExtend = this.resultInputService.extendFromResultInput(data);
                    resultInputExtend.data = this.resultInputDetailService.extendFromResultInputDetailList
                            (resultInputDetailList);

                    l.add(resultInputExtend);
                });

                m.put("data", l);

                result.add(m);
            }

        });


        return CommonResult.success("查询成功", result);
    }

    /**
     * 更改状态
     *
     * @param inputId
     * @param params
     * @param session
     * @return
     */
    @RequestMapping(value = "status/{inputId}", method = RequestMethod.PUT)
    public CommonResult submitOriginRecord(@PathVariable("inputId") Integer inputId, @RequestBody Map<String, Object>
            params, HttpSession session) {

        ResultInput resultInput = this.resultInputService.queryById(inputId);
        if (resultInput == null) {
            return CommonResult.failure("提交失败，不存在的记录");
        }

        // 录入中，待审核，未通知，已通过
        String status = (String) params.get(Constant.STATUS);
        String reason = (String) params.get(Constant.REASON);

        if (Validator.checkEmpty(status)) {
            return CommonResult.failure("修改失败，缺少参数");
        }

        // checker
        Identity identity = (Identity) session.getAttribute(Constant.IDENTITY);
        String identityRole = identity.getRole();
        Integer checkerId = Integer.valueOf(identity.getId());
        String checkerName = this.userService.queryById(checkerId).getName();

        if (status.equals(Constant.DAI_SHEN_HE)) { // 提交，待审核

            // 状态改为'待审核'
            resultInput.setStatus(Constant.DAI_SHEN_HE);
            this.resultInputService.update(resultInput);

            return CommonResult.success("提交成功");

        } else if (status.equals(Constant.WEI_TONG_GUO)) { // 未通过

            // 具有通过和未通过两项权利的人只有主管和ADMIN
            if (!this.userService.checkManager(identityRole) && !this.userService.checkAdmin(identityRole)) {
                return CommonResult.failure("无此权限");
            }

            if (Validator.checkEmpty(reason)) {
                reason = "<未说明原因>";
            }

            resultInput.setCheckerId(checkerId);

            resultInput.setStatus(Constant.WEI_TONG_GUO);
            resultInput.setReason(reason);
            this.resultInputService.update(resultInput);

            return CommonResult.success("操作成功");

        } else if (status.equals(Constant.YI_TONG_GUO)) { // 通过，已通过

            // 具有通过和未通过两项权利的人只有主管和ADMIN
            if (!this.userService.checkManager(identityRole) && !this.userService.checkAdmin(identityRole)) {
                return CommonResult.failure("无此权限");
            }

            resultInput.setCheckerId(checkerId);

            resultInput.setStatus(Constant.YI_TONG_GUO);
            this.resultInputService.update(resultInput);

            return CommonResult.success("操作成功");
        } else {
            return CommonResult.failure("参数错误");
        }

    }


    /**
     * 查询一个用户的一个亚类的所有检查记录并下载
     *
     * @param userId
     * @param params
     * @return
     */
    @RequestMapping(value = "download/{userId}", method = RequestMethod.POST)
    public CommonResult downloadByUserIdAndTypeWithSecondId(
            @PathVariable("userId") Integer userId,
            @RequestBody Map<String, Object> params) {

        String type = (String) params.get("type");
        Integer secondId = (Integer) params.get(Constant.SECOND_ID);

        String secondName = this.categorySecondService.queryById(secondId).getName();

        Date beginTime = TimeUtil.parseTime((String) params.get("beginTime"));
        Date endTime = TimeUtil.parseTime((String) params.get("endTime"));

        // 查询开始
        // ResultInput record = new ResultInput().setUserId(userId).setSecondId(secondId);
        // List<ResultInput> resultInputList = this.resultInputService.queryListByWhere(record);
        Example example = new Example(ResultInput.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("userId", userId);
        criteria.andEqualTo("secondId", secondId);

        example.setOrderByClause("time DESC"); // 倒叙

        // 时间
        if (beginTime != null && endTime != null) {
            criteria.andBetween("time", beginTime, endTime);
        }

        List<ResultInput> resultInputList = this.resultInputService.getMapper().selectByExample(example);

        if (resultInputList != null && resultInputList.size() == 0) {
            return CommonResult.failure("此亚类下不存在检查记录");
        }

        List<ResultInputExtend> resultInputExtendList = this.resultInputService.extendFromResultInputList
                (resultInputList);

        resultInputExtendList.forEach(resultInputExtend -> {

            ResultInputDetail resultInputDetail = new ResultInputDetail().setResultInputId(resultInputExtend.getId());
            List<ResultInputDetail> resultInputDetailList = this.resultInputDetailService.queryListByWhere
                    (resultInputDetail);

            resultInputExtend.data = this.resultInputDetailService.extendFromResultInputDetailList
                    (resultInputDetailList);
        });

        // 至此，resultInputExtendList就是全部内容

        // 新建表格
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet inputSheet = workbook.createSheet();
        inputSheet.setDefaultColumnWidth(20);
        inputSheet.setDefaultRowHeight((short) (1.6 * 256));

        List<XSSFRow> rowPointor = new ArrayList<>(50);
        for (int i = 0; i < 50; i++) {
            rowPointor.add(inputSheet.createRow(i));
        }


        if (type.equals("化验")) {
            // 第一行，检查亚类名称，检查记录数 + 2 （化验项目名称、参考值）
            {
                XSSFRow firstRow = rowPointor.get(0);
                XSSFCell firstRowCell = firstRow.createCell((short) 0);
                firstRowCell.setCellValue(secondName);

                XSSFFont firstFont = workbook.createFont();
                firstFont.setColor(XSSFFont.COLOR_RED); // 红色
                firstFont.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD); // 加粗
                firstFont.setFontHeightInPoints((short) 14);

                XSSFCellStyle firstStyle = workbook.createCellStyle();
                firstStyle.setFont(firstFont);
                firstStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);

                firstRowCell.setCellStyle(firstStyle);

                inputSheet.addMergedRegion(new CellRangeAddress(
                        0, // first firstRow (0-based)
                        0, // last firstRow (0-based)
                        0, // first column (0-based)
                        resultInputExtendList.size() + 1 // last column (0-based)
                ));
            }


            {
                // 第二行
                XSSFRow row = rowPointor.get(1);
                row.createCell(0).setCellValue("化验项目");
                row.createCell(1).setCellValue("参考值");

                for (int i = 0; i < resultInputExtendList.size(); i++) {
                    row.createCell(i + 2).setCellValue(resultInputExtendList.get(i).getHospital() + " " +
                            TimeUtil.parseTime(resultInputExtendList.get(i).getTime()));
                }
            }

            // 前两列可以一次填充
            List<CategoryThird> categoryThirdList = this.categoryThirdService.queryListByWhere(new CategoryThird()
                    .setSecondId(secondId));

            {
                for (int i = 0; i < categoryThirdList.size(); i++) {
                    XSSFRow row = rowPointor.get(i + 2);
                    row.createCell((short) 0).setCellValue(categoryThirdList.get(i).getName());
                    row.createCell((short) 1).setCellValue(categoryThirdList.get(i).getReferenceValue());
                }
            }

            // // 剩下的具体数据按列填充
            {
                for (int i = 0; i < resultInputExtendList.size(); i++) {

                    // 要填充的数据
                    List<ResultInputDetailExtend> data = resultInputExtendList.get(i).data;

                    for (int j = 0; j < categoryThirdList.size(); j++) {
                        Integer thirdId = categoryThirdList.get(j).getId();

                        for (ResultInputDetailExtend datum : data) {
                            if (datum.getThirdId().equals(thirdId)) {
                                rowPointor.get(j + 2).createCell((short) (i + 2)).setCellValue(datum
                                        .getValue());
                            }
                        }

                    }
                }
            }

            String fileName = this.propertyService.filePath + "/input/" + userId + "-" + secondId + ".xlsx";

            try {
                FileOutputStream out = new FileOutputStream(new File(fileName));
                // OutputStream out = response.getOutputStream();
                workbook.write(out);
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
                return CommonResult.failure("下载失败");
            }

            return CommonResult.success("下载成功", "/input/" + userId + "-" + secondId + ".xlsx");
        } else {

            // 第一行，检查亚类名称，检查记录数 + 2 （化验项目名称、参考值）
            {
                XSSFRow firstRow = rowPointor.get(0);
                XSSFCell firstRowCell = firstRow.createCell((short) 0);
                firstRowCell.setCellValue(secondName);

                XSSFFont firstFont = workbook.createFont();
                firstFont.setColor(XSSFFont.COLOR_RED); // 红色
                firstFont.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD); // 加粗
                firstFont.setFontHeightInPoints((short) 14);

                XSSFCellStyle firstStyle = workbook.createCellStyle();
                firstStyle.setFont(firstFont);
                firstStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);

                firstRowCell.setCellStyle(firstStyle);

                inputSheet.addMergedRegion(new CellRangeAddress(
                        0, // first firstRow (0-based)
                        0, // last firstRow (0-based)
                        0, // first column (0-based)
                        2 // last column (0-based)
                ));
            }

            {
                // 第二行
                XSSFRow row = rowPointor.get(1);
                row.createCell(0).setCellValue("检查日期");
                row.createCell(1).setCellValue("检查医院");
                row.createCell(2).setCellValue("检查结果");
            }

            for (int i = 0; i < resultInputExtendList.size(); i++) {
                XSSFRow row = rowPointor.get(i + 2);
                row.createCell(0).setCellValue(TimeUtil.parseTime(resultInputExtendList.get(i).getTime()));
                row.createCell(1).setCellValue(resultInputExtendList.get(i).getHospital());
                row.createCell(2).setCellValue(resultInputExtendList.get(i).data.get(0).getValue());
            }


            String fileName = this.propertyService.filePath + "/input/" + userId + "-" + secondId + ".xlsx";

            try {
                FileOutputStream out = new FileOutputStream(new File(fileName));
                // OutputStream out = response.getOutputStream();
                workbook.write(out);
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
                return CommonResult.failure("下载失败");
            }

            return CommonResult.success("下载成功", "/input/" + userId + "-" + secondId + ".xlsx");
        }
    }
}
