package com.yhch.controller;

import com.yhch.bean.CommonResult;
import com.yhch.pojo.ResultInputDetail;
import com.yhch.service.CategoryThirdService;
import com.yhch.service.ResultInputDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * detail表
 * Created by zlren on 2017/7/2.
 */
@Controller
@RequestMapping("detail")
public class ResultInputDetailController {

    @Autowired
    private ResultInputDetailService resultInputDetailService;

    @Autowired
    private CategoryThirdService categoryThirdService;


    /**
     * 为一条result-input记录添加详细信息（多个result-input-detail）
     *
     * @param params
     * @return
     */
    @RequestMapping(method = RequestMethod.PUT)
    @ResponseBody
    public CommonResult addResultInputDetail(@RequestBody Map<Integer, String> params) {

        List<ResultInputDetail> dataToSaveList = new ArrayList<>();

        for (Map.Entry<Integer, String> next : params.entrySet()) {
            if (next.getValue() != null) {
                ResultInputDetail resultInputDetail = new ResultInputDetail();
                resultInputDetail.setId(next.getKey());
                resultInputDetail.setValue(next.getValue());
                dataToSaveList.add(resultInputDetail);
            }
        }

        // 批量保存，放在Service层去实现感觉稳一些，事务的支持
        this.resultInputDetailService.save(dataToSaveList);

        return CommonResult.success("修改成功");
    }
}
