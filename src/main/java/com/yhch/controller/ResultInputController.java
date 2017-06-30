package com.yhch.controller;

import com.yhch.bean.CommonResult;
import com.yhch.bean.Constant;
import com.yhch.bean.input.ResultInputDetailExtend;
import com.yhch.bean.input.ResultInputExtend;
import com.yhch.pojo.ResultInput;
import com.yhch.pojo.ResultInputDetail;
import com.yhch.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by zlren on 2017/6/21.
 */
@RequestMapping("input")
@Controller
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

    /**
     * 在input表增加一条记录
     *
     * @param params
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public CommonResult addResultInput(@RequestBody Map<String, Object> params) {

        Integer userId = (Integer) params.get("userId");
        Integer secondId = (Integer) params.get("secondId");
        Integer checkerId = (Integer) params.get("checkerId");
        Integer inputerId = (Integer) params.get("inputerId");
        String status = Constant.LU_RU_ZHONG; // 初始状态为录入中
        String note = (String) params.get(Constant.NOTE);

        Date time;
        try {
            time = new SimpleDateFormat("yyyy-MM-dd").parse((String) params.get(Constant.TIME));
        } catch (ParseException e) {
            e.printStackTrace();
            return CommonResult.failure("添加失败，日期解析错误");
        }

        ResultInput resultInput = new ResultInput();
        resultInput.setUserId(userId);
        resultInput.setSecondId(secondId);
        resultInput.setCheckerId(checkerId);
        resultInput.setInputerId(inputerId);
        resultInput.setStatus(status);
        resultInput.setNote(note);
        resultInput.setTime(time);

        this.resultInputService.save(resultInput);

        return CommonResult.success("添加成功");
    }


    /**
     * 为一条result-input记录添加详细信息
     *
     * @param params
     * @return
     */
    @RequestMapping(value = "detail", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult addResultInputDetail(@RequestBody Map<String, Object> params) {
        Integer inputId = (Integer) params.get("inputId");
        List<Map<Integer, Object>> dataList = (List<Map<Integer, Object>>) params.get("data");
        this.resultInputDetailService.saveDetails(inputId, dataList);
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

        ResultInput resultInput = this.resultInputService.queryById(inputId);
        ResultInputExtend resultInputExtend = new ResultInputExtend();
        BeanUtils.copyProperties(resultInput, resultInputExtend);

        resultInputExtend.userName = this.userService.queryById(resultInput.getUserId()).getName();
        resultInputExtend.secondName = this.categorySecondService.queryById(resultInput.getSecondId()).getName();
        resultInputExtend.inputerName = this.userService.queryById(resultInput.getInputerId()).getName();
        resultInputExtend.checkerName = this.userService.queryById(resultInput.getCheckerId()).getName();

        ResultInputDetail record = new ResultInputDetail();
        record.setResultInputId(resultInput.getId());
        List<ResultInputDetail> resultInputDetailList = this.resultInputDetailService.queryListByWhere(record);

        resultInputDetailList.forEach(resultInputDetail -> {
            ResultInputDetailExtend resultInputDetailExtend = new ResultInputDetailExtend();
            BeanUtils.copyProperties(resultInputDetail, resultInputDetailExtend);
            resultInputDetailExtend.thirdName = this.categoryThirdService.queryById(resultInputDetail.getThirdId())
                    .getName();
            resultInputExtend.resultInputDetailList.add(resultInputDetailExtend);
        });

        return CommonResult.success("查询成功", resultInputExtend);
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
}
