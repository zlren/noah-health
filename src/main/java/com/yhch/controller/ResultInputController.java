package com.yhch.controller;

import com.github.pagehelper.PageInfo;
import com.yhch.bean.CommonResult;
import com.yhch.bean.Constant;
import com.yhch.bean.Identity;
import com.yhch.bean.PageResult;
import com.yhch.bean.input.ResultInputDetailExtend;
import com.yhch.bean.input.ResultInputExtend;
import com.yhch.pojo.CategoryThird;
import com.yhch.pojo.ResultInput;
import com.yhch.pojo.ResultInputDetail;
import com.yhch.service.*;
import com.yhch.util.TimeUtil;
import com.yhch.util.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
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

        List<ResultInputDetailExtend> resultInputDetailExtendList = new ArrayList<>();

        resultInputDetailList.forEach(resultInputDetail -> {

            CategoryThird categoryThird = this.categoryThirdService.queryById(resultInputDetail.getThirdId());
            String thirdName = categoryThird.getName();
            String referenceValue = categoryThird.getReferenceValue();
            String systemCategory = categoryThird.getSystemCategory();
            String hospital = categoryThird.getHospital();

            resultInputDetailExtendList.add(new
                    ResultInputDetailExtend(resultInputDetail, thirdName, referenceValue, systemCategory, hospital));
        });

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
        Set<Integer> memberSet = this.userService.queryMemberIdSetUnderEmployee(identity);

        List<ResultInput> resultInputList = this.resultInputService.queryInputList(memberSet, status, userName,
                inputerName, checkerName, secondName, hospital, time, pageNow, pageSize);

        PageResult pageResult = new PageResult(new PageInfo<>(resultInputList));

        List<ResultInputExtend> resultInputExtendList = new ArrayList<>();


        resultInputList.forEach(resultInput -> {

            String userNameExtend = this.userService.queryById(resultInput.getUserId()).getName();
            String checkerNameExtend = null;
            if (resultInput.getCheckerId() != null) {
                checkerNameExtend = this.userService.queryById(resultInput.getCheckerId()).getName();
            }
            String inputerNameExtend = this.userService.queryById(resultInput.getInputerId()).getName();
            String secondNameExtend = this.categorySecondService.queryById(resultInput.getSecondId()).getName();

            resultInputExtendList.add(new ResultInputExtend(resultInput, userNameExtend, secondNameExtend,
                    inputerNameExtend, checkerNameExtend));
        });

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
}
