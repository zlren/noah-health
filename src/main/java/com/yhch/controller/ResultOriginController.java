package com.yhch.controller;

import com.github.pagehelper.PageInfo;
import com.yhch.bean.CommonResult;
import com.yhch.bean.Constant;
import com.yhch.bean.PageResult;
import com.yhch.pojo.ResultOrigin;
import com.yhch.service.ResultOriginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 原始数据
 * Created by zlren on 2017/6/21.
 */
@RequestMapping("origin")
@Controller
public class ResultOriginController {

    @Autowired
    private ResultOriginService resultOriginService;


    /**
     * 上传原始数据
     *
     * @param params
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public CommonResult addResultOrigin(@RequestBody Map<String, Object> params) {

        Integer userId = (Integer) params.get(Constant.USER_ID);
        Integer secondId = (Integer) params.get(Constant.SECOND_ID);
        Integer uploaderId = (Integer) params.get(Constant.UPLOADER_ID);

        String isPass = "未通过";
        String isInput = "未录入";

        return CommonResult.success("文件上传成功");
    }

    /**
     * 删除原始数据
     *
     * @param originId
     * @return
     */
    @RequestMapping(value = "{originId}", method = RequestMethod.DELETE)
    @ResponseBody
    public CommonResult deleteResultOrigin(@PathVariable("originId") Integer originId) {

        if (this.resultOriginService.queryById(originId) != null) {
            return CommonResult.failure("删除失败，不存在的文件");
        }

        this.resultOriginService.deleteById(originId);
        return CommonResult.success("删除成功");
    }


    /**
     * 修改原始数据
     *
     * @param originId
     * @param params
     * @return
     */
    @RequestMapping(value = "{originId}", method = RequestMethod.PUT)
    @ResponseBody
    public CommonResult updateResultOrigin(@PathVariable("originId") Integer originId, @RequestBody Map<String,
            Object> params) {

        ResultOrigin resultOrigin = this.resultOriginService.queryById(originId);

        if (resultOrigin == null) {
            return CommonResult.failure("不存在的文件");
        }

        Integer userId = (Integer) params.get(Constant.USER_ID);
        Integer secondId = (Integer) params.get(Constant.SECOND_ID);
        Integer uploaderId = (Integer) params.get(Constant.UPLOADER_ID);
        String isPass = (String) params.get(Constant.IS_PASS);
        String isInput = (String) params.get(Constant.IS_INPUT);

        if (userId != null) {
            resultOrigin.setUserId(userId);
        }
        if (secondId != null) {
            resultOrigin.setSecondId(secondId);
        }
        if (uploaderId != null) {
            resultOrigin.setUploaderId(uploaderId);
        }
        if (isPass != null) {
            resultOrigin.setIsPass(isPass);
        }
        if (isInput != null) {
            resultOrigin.setIsInput(isInput);
        }

        this.resultOriginService.update(resultOrigin);

        return CommonResult.success("修改成功");
    }


    /**
     * 分页查询通过或者未通过的列表
     *
     * @param isPass
     * @param params
     * @return
     */
    @RequestMapping(value = "{isPass}/list", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult showPassOrNotResultOriginList(@PathVariable("isPass") String isPass, @RequestBody Map<String,
            Object> params) {

        Integer pageNow = (Integer) params.get(Constant.PAGE_NOW);
        Integer pageSize = (Integer) params.get(Constant.PAGE_SIZE);

        ResultOrigin record = new ResultOrigin();
        if (isPass.equals(Constant.WEITONGGUO) || isPass.equals(Constant.YITONGGUO)) {
            record.setIsPass(isPass);
        } else {
            return CommonResult.failure("参数错误");
        }

        PageInfo<ResultOrigin> resultOriginPageInfo = this.resultOriginService.queryPageListByWhere(pageNow,
                pageSize, record);

        return CommonResult.success("查询成功", new PageResult(resultOriginPageInfo));
    }


    /**
     * 根据userId查询对应的所有原始数据列表
     *
     * @param userId
     * @param params
     * @return
     */
    @RequestMapping(value = "user/{userId}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult showResultOriginByUser(@PathVariable("userId") Integer userId, @RequestBody Map<String,
            Object> params) {

        Integer pageNow = (Integer) params.get(Constant.PAGE_NOW);
        Integer pageSize = (Integer) params.get(Constant.PAGE_SIZE);

        ResultOrigin record = new ResultOrigin();
        record.setUserId(userId);

        PageInfo<ResultOrigin> resultOriginPageInfo = this.resultOriginService.queryPageListByWhere(pageNow,
                pageSize, record);

        return CommonResult.success("查询成功", new PageResult(resultOriginPageInfo));
    }
}
