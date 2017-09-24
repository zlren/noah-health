package com.noahhealth.controller;

import com.noahhealth.bean.CommonResult;
import com.noahhealth.pojo.ResultInputDetail;
import com.noahhealth.service.ResultInputDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * detail表
 * Created by zlren on 2017/7/2.
 */
@RestController
@RequestMapping("detail")
@Slf4j
public class ResultInputDetailController {

    @Autowired
    private ResultInputDetailService resultInputDetailService;

    /**
     * 为一条result-input记录添加详细信息（多个result-input-detail）
     *
     * @param params
     * @return
     */
    @RequestMapping(method = RequestMethod.PUT)
    public CommonResult addResultInputDetail(@RequestBody Map<String, String> params) {

        List<ResultInputDetail> dataToSaveList = new ArrayList<>();

        // 每一条检查记录有两个值，value和normal
        // id-value: value
        // id-normal: true/false
        for (Map.Entry<String, String> next : params.entrySet()) {
            if (next.getValue() != null) {

                String key = next.getKey();
                String[] split = key.split("-");

                ResultInputDetail resultInputDetail = new ResultInputDetail();
                resultInputDetail.setId(Integer.valueOf(split[0]));
                if (split[1].equals("normal")) {
                    if (next.getValue().equals("true")) {
                        resultInputDetail.setNormal(true);
                    } else {
                        resultInputDetail.setNormal(false);
                    }
                } else if (split[1].equals("value")) {
                    resultInputDetail.setValue(next.getValue());
                }
                // else if (split[1].equals("note")) {
                //     // 取消掉了
                //     // resultInputDetail.setNote(next.getValue());
                // }

                dataToSaveList.add(resultInputDetail);
            }
        }

        // 批量保存，放在Service层去实现感觉稳一些，事务的支持
        this.resultInputDetailService.save(dataToSaveList);

        return CommonResult.success("修改成功");
    }
}
