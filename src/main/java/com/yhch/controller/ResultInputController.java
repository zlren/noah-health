package com.yhch.controller;

import com.github.pagehelper.PageInfo;
import com.yhch.bean.CommonResult;
import com.yhch.bean.Constant;
import com.yhch.bean.Identity;
import com.yhch.bean.PageResult;
import com.yhch.bean.input.ResultInputDetailExtend;
import com.yhch.bean.input.ResultInputExtend;
import com.yhch.bean.user.UserExtend;
import com.yhch.pojo.ResultInput;
import com.yhch.pojo.ResultInputDetail;
import com.yhch.pojo.User;
import com.yhch.service.*;
import com.yhch.util.TimeUtil;
import com.yhch.util.Validator;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 化验、医技数据管理
 * Created by zlren on 2017/6/21.
 */
@RequestMapping("input")
@Controller
public class ResultInputController {

    private static final Logger logger = LoggerFactory.getLogger(ResultInputController.class);

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
     * 在input表增加一条记录
     *
     * @param params
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
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
     * 根据inputId查询一个化验表的信息
     *
     * @param inputId
     * @return
     */
    @RequestMapping(value = "{inputId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult queryResultInputById(@PathVariable("inputId") Integer inputId) {

        ResultInputDetail record = new ResultInputDetail();
        record.setResultInputId(inputId);
        List<ResultInputDetail> resultInputDetailList = this.resultInputDetailService.queryListByWhere(record);

        List<ResultInputDetailExtend> resultInputDetailExtendList = this.resultInputDetailService
                .extendFromResultInputDetailList(resultInputDetailList);

        return CommonResult.success("查询成功", resultInputDetailExtendList);
    }


    /**
     * 删除input记录，级联删除detail表
     *
     * @param inputId
     * @return
     */
    @RequestMapping(value = "{inputId}", method = RequestMethod.DELETE)
    @ResponseBody
    public CommonResult deleteResultInputById(@PathVariable("inputId") Integer inputId) {

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
    @ResponseBody
    public CommonResult queryResultInputUserList(@RequestBody Map<String, Object> params, HttpSession session) {

        Integer pageNow = (Integer) params.get(Constant.PAGE_NOW);
        Integer pageSize = (Integer) params.get(Constant.PAGE_SIZE);
        String userName = (String) params.get("userName");
        String memberNum = (String) params.get("memberNum");
        Identity identity = (Identity) session.getAttribute(Constant.IDENTITY);

        List<User> userList = this.resultInputService.queryResultInputUserList(identity, userName, memberNum,
                pageNow, pageSize);
        PageResult pageResult = new PageResult(new PageInfo<>(userList));

        List<UserExtend> userExtendList = this.userService.extendFromUser(userList);
        pageResult.setData(userExtendList);

        return CommonResult.success("查询成功", pageResult);
    }


    /**
     * 档案部查的时候，直接查那些uploaderId是自己的所有记录，不要再嵌套一层user列表
     *
     * @return
     */
    @RequestMapping(value = "list_by_arc", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult queryResultInputListByArc(@RequestBody Map<String, Object> params, HttpSession session) {

        Integer pageNow = (Integer) params.get(Constant.PAGE_NOW);
        Integer pageSize = (Integer) params.get(Constant.PAGE_SIZE);
        String userName = (String) params.get("userName");
        String memberNum = (String) params.get("memberNum");
        Date beginTime = TimeUtil.parseTime((String) params.get("beginTime"));
        Date endTime = TimeUtil.parseTime((String) params.get("endTime"));
        String status = (String) params.get(Constant.STATUS);
        Identity identity = (Identity) session.getAttribute(Constant.IDENTITY);

        List<ResultInput> resultInputList = this.resultInputService.queryInputListByArc(pageNow, pageSize, userName,
                memberNum, beginTime, endTime, status, identity);
        PageResult pageResult = new PageResult(new PageInfo<>(resultInputList));

        logger.info("查询的结果的条数：{}", pageResult.getRowCount());

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
    @ResponseBody
    public CommonResult queryResultAndDetailListByUserId(@PathVariable("userId") Integer userId, HttpSession session,
                                                         @RequestBody Map<String, Object> params) {

        Identity identity = (Identity) session.getAttribute(Constant.IDENTITY);
        String type = (String) params.get("type");
        String status = (String) params.get(Constant.STATUS);
        Integer secondId = (Integer) params.get(Constant.SECOND_ID);
        Date beginTime = TimeUtil.parseTime((String) params.get("beginTime"));
        Date endTime = TimeUtil.parseTime((String) params.get("endTime"));

        List<ResultInput> resultInputList = this.resultInputService.queryResultAndDetailListByUserId(identity,
                userId, type, status, secondId, beginTime, endTime);

        List<ResultInputExtend> resultInputExtendList = this.resultInputService.extendFromResultInputList
                (resultInputList);

        resultInputExtendList.forEach(resultInputExtend -> {

            ResultInputDetail resultInputDetailRecord = new ResultInputDetail();
            resultInputDetailRecord.setResultInputId(resultInputExtend.getId());
            List<ResultInputDetail> resultInputDetailList = this.resultInputDetailService.queryListByWhere
                    (resultInputDetailRecord);

            resultInputExtend.data = this.resultInputDetailService.extendFromResultInputDetailList
                    (resultInputDetailList);
        });

        return CommonResult.success("查询成功", resultInputExtendList);
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
    @ResponseBody
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
        Integer checkerId = Integer.valueOf(identity.getId());
        String checkerName = this.userService.queryById(checkerId).getName();

        if (status.equals(Constant.DAI_SHEN_HE)) { // 提交，待审核

            // 状态改为'待审核'
            resultInput.setStatus(Constant.DAI_SHEN_HE);
            this.resultInputService.update(resultInput);

            return CommonResult.success("提交成功");

        } else if (status.equals(Constant.WEI_TONG_GUO)) { // 未通过

            if (Validator.checkEmpty(reason)) {
                reason = "<未说明原因>";
            }

            resultInput.setCheckerId(checkerId);

            resultInput.setStatus(Constant.WEI_TONG_GUO);
            resultInput.setReason(reason);
            this.resultInputService.update(resultInput);

            return CommonResult.success("操作成功");

        } else if (status.equals(Constant.YI_TONG_GUO)) { // 通过，已通过

            resultInput.setCheckerId(checkerId);

            resultInput.setStatus(Constant.YI_TONG_GUO);
            this.resultInputService.update(resultInput);

            return CommonResult.success("操作成功");
        } else {
            return CommonResult.failure("参数错误");
        }

    }

    /**
     * 生成excel表打印
     *
     * @param inputId
     * @return
     */
    @RequestMapping(value = "download/{inputId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult downloadResultInputWithDetail(@PathVariable("inputId") Integer inputId) {

        ResultInput resultInput = this.resultInputService.queryById(inputId);
        if (resultInput == null) {
            return CommonResult.failure("下载失败，不存在的记录");
        }

        ResultInputDetail record = new ResultInputDetail();
        record.setResultInputId(inputId);
        List<ResultInputDetail> resultInputDetailList = this.resultInputDetailService.queryListByWhere(record);
        List<ResultInputDetailExtend> resultInputDetailExtendList = this.resultInputDetailService
                .extendFromResultInputDetailList(resultInputDetailList);

        String secondName = this.categorySecondService.queryById(resultInput.getSecondId()).getName();

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet inputSheet = workbook.createSheet(secondName);
        inputSheet.setDefaultColumnWidth(13);
        inputSheet.setDefaultRowHeight((short) (1.6 * 256));

        // 第一行，6个单元格合并，检查亚类
        {
            XSSFRow firstRow = inputSheet.createRow((short) 0);
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
                    0, //first firstRow (0-based)
                    0, //last firstRow (0-based)
                    0, //first column (0-based)
                    4 //last column (0-based)
            ));
        }

        // 第二行表头，5个单元格分别是，检查项目名称，系统分类，参考值及单位，301医院和检查结果
        {
            XSSFRow secondRow = inputSheet.createRow((short) 1);

            XSSFFont boldFont = workbook.createFont();
            boldFont.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD); // 加粗

            XSSFCellStyle boldStyle = workbook.createCellStyle();
            boldStyle.setFont(boldFont);
            boldStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);


            XSSFCell cell0 = secondRow.createCell((short) 0);
            cell0.setCellStyle(boldStyle);
            cell0.setCellValue("检查项目名称");

            XSSFCell cell1 = secondRow.createCell((short) 1);
            cell1.setCellStyle(boldStyle);
            cell1.setCellValue("系统分类");

            XSSFCell cell2 = secondRow.createCell((short) 2);
            cell2.setCellStyle(boldStyle);
            cell2.setCellValue("参考值及单位");

            XSSFCell cell3 = secondRow.createCell((short) 3);
            cell3.setCellStyle(boldStyle);
            cell3.setCellValue("301医院");

            XSSFCell cell4 = secondRow.createCell((short) 4);
            cell4.setCellStyle(boldStyle);
            cell4.setCellValue("检查结果");
        }

        {
            int i = 2;
            for (ResultInputDetailExtend resultInputDetailExtend : resultInputDetailExtendList) {
                XSSFRow row = inputSheet.createRow((short) i);

                XSSFCell cell0 = row.createCell((short) 0);
                cell0.setCellValue(resultInputDetailExtend.thirdName);

                // XSSFCell cell1 = row.createCell((short) 1);
                // cell1.setCellValue(resultInputDetailExtend.systemCategory);

                XSSFCell cell2 = row.createCell((short) 2);
                cell2.setCellValue(resultInputDetailExtend.referenceValue);

                XSSFCell cell3 = row.createCell((short) 3);
                cell3.setCellValue(resultInputDetailExtend.hospital);

                XSSFCell cell4 = row.createCell((short) 4);
                cell4.setCellValue(resultInputDetailExtend.getValue());

                i++;
            }
        }

        String fileName = this.propertyService.filePath + "input/" + inputId + ".xlsx";

        try {
            FileOutputStream out = new FileOutputStream(new File(fileName));
            // OutputStream out = response.getOutputStream();
            workbook.write(out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            return CommonResult.failure("下载失败");
        }

        return CommonResult.success("下载成功", "/input/" + inputId + ".xlsx");
    }
}
