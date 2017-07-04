package com.yhch.controller;

import com.github.pagehelper.PageInfo;
import com.yhch.bean.CommonResult;
import com.yhch.bean.Constant;
import com.yhch.bean.Identity;
import com.yhch.bean.PageResult;
import com.yhch.bean.input.ResultInputDetailExtend;
import com.yhch.bean.input.ResultInputExtend;
import com.yhch.pojo.ResultInput;
import com.yhch.pojo.ResultInputDetail;
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
import java.util.*;

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
        Integer secondId = (Integer) params.get("secondId");
        Integer checkerId = (Integer) params.get("checkerId");
        Identity identity = (Identity) session.getAttribute(Constant.IDENTITY);
        Integer inputerId = Integer.valueOf(identity.getId());
        String status = Constant.LU_RU_ZHONG; // 初始状态为录入中
        String note = (String) params.get(Constant.NOTE);
        String hospital = (String) params.get("hospital");
        Date time = TimeUtil.parseTime((String) params.get(Constant.TIME));

        ResultInput resultInput = new ResultInput();
        resultInput.setUserId(userId);
        resultInput.setSecondId(secondId);
        resultInput.setCheckerId(checkerId);
        resultInput.setInputerId(inputerId);
        resultInput.setStatus(status);
        resultInput.setNote(note);
        resultInput.setHospital(hospital);
        resultInput.setTime(time);

        // 级联插入
        this.resultInputService.saveInputAndEmptyDetail(resultInput);

        Map<String, Object> result = new HashMap<>();
        result.put("resultInputId", resultInput.getId());
        result.put("secondName", this.categorySecondService.queryById(secondId).getName());

        return CommonResult.success("添加成功", result);
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
     * 根据userId查询此member的所有input记录，每个记录对应一个亚类
     * 点击去才具体显示这个亚类的每个检查项目，这些信息从detail表中查询
     *
     * @param params
     * @param session
     * @return
     */
    @RequestMapping(value = "list", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult queryResultInputByUserId(@RequestBody Map<String, Object> params, HttpSession session) {

        Integer pageNow = (Integer) params.get(Constant.PAGE_NOW);
        Integer pageSize = (Integer) params.get(Constant.PAGE_SIZE);

        String status = (String) params.get(Constant.STATUS);
        String userName = (String) params.get("userName");
        String inputerName = (String) params.get("inputerName");
        String checkerName = (String) params.get("checkerName");
        String secondName = (String) params.get("secondName");
        String hospital = (String) params.get("hospital");
        Date time = TimeUtil.parseTime((String) params.get(Constant.TIME));

        Identity identity = (Identity) session.getAttribute(Constant.IDENTITY);
        String role = identity.getRole();

        // 一级用户无此权限
        if (role.equals(Constant.USER_1)) {
            return CommonResult.failure("无此权限");
        }

        Set<Integer> usersSet = new HashSet<>();
        if (this.userService.checkMember(identity.getRole())) { // 二级、三级用户
            // member只能查看自己的数据，且是已通过的
            usersSet.add(Integer.valueOf(identity.getId()));
            status = Constant.YI_TONG_GUO;
        } else {
            // 职员
            usersSet = this.userService.queryMemberIdSetUnderEmployee(identity);
        }

        List<ResultInput> resultInputList = this.resultInputService.queryInputList(usersSet, status, userName,
                inputerName, checkerName, secondName, hospital, time, pageNow, pageSize);

        PageResult pageResult = new PageResult(new PageInfo<>(resultInputList));

        List<ResultInputExtend> resultInputExtendList = this.resultInputService.extendFromResultInputList
                (resultInputList);

        pageResult.setData(resultInputExtendList);

        return CommonResult.success("查询成功", pageResult);
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
     * @param params
     * @return
     */
    @RequestMapping(value = "download", method = RequestMethod.GET)
    public CommonResult downloadResultInputWithDetail(@RequestBody Map<String, Integer> params) {

        Integer inputId = params.get("inputId");

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

        // 第一行，6个单元格合并，检查亚类
        {
            XSSFRow firstRow = inputSheet.createRow((short) 0);
            XSSFCell firstRowCell = firstRow.createCell((short) 0);
            firstRowCell.setCellValue("血常规");

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

            List<String> content = Arrays.asList("检查项目名称", "系统分类", "参考值及单位", "301医院", "检查结果");
            for (int i = 0; i < 5; i++) {
                XSSFCell cell = secondRow.createCell((short) i);
                cell.setCellStyle(boldStyle);
                cell.setCellValue(content.get(i));
            }
        }

        {
            int i = 2;
            resultInputDetailExtendList.forEach(resultInputDetailExtend -> {
                XSSFRow row = inputSheet.createRow((short) i);

                XSSFCell cell0 = row.createCell((short) 0);
                cell0.setCellValue(resultInputDetailExtend.thirdName);

                XSSFCell cell1 = row.createCell((short) 1);
                cell1.setCellValue(resultInputDetailExtend.systemCategory);

                XSSFCell cell2 = row.createCell((short) 2);
                cell2.setCellValue(resultInputDetailExtend.referenceValue);

                XSSFCell cell3 = row.createCell((short) 3);
                cell3.setCellValue(resultInputDetailExtend.hospital);

                XSSFCell cell4 = row.createCell((short) 4);
                cell4.setCellValue(resultInputDetailExtend.getValue());
            });

        }

        String fileName = this.propertyService.filePath + "input/" + inputId + ".xlsx";

        try {
            FileOutputStream out = new FileOutputStream(
                    new File(fileName));
            workbook.write(out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            return CommonResult.failure("下载失败");
        }

        return CommonResult.success("下载成功", "/" + fileName);
    }
}
