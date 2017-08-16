package com.yhch.controller;

import com.yhch.bean.CommonResult;
import com.yhch.pojo.HealthCategoryFirst;
import com.yhch.pojo.HealthCategorySecond;
import com.yhch.service.HealthCategoryFirstService;
import com.yhch.service.HealthCategorySecondService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zlren on 17/7/19.
 */
@RequestMapping("health_category")
@RestController
public class HealthCategoryController {

    @Autowired
    private HealthCategoryFirstService healthCategoryFirstService;

    @Autowired
    private HealthCategorySecondService healthCategorySecondService;


    /**
     * 分级查询
     *
     * @return
     */
    @RequestMapping(value = "level", method = RequestMethod.GET)
    public CommonResult queryHealthCategoryLevel() {

        Map<String, List<HealthCategorySecond>> result = new HashMap<>();

        List<HealthCategoryFirst> healthCategoryFirstList = this.healthCategoryFirstService.queryListByWhere(null);

        healthCategoryFirstList.forEach(healthCategoryFirst -> {

            HealthCategorySecond healthCategorySecond = new HealthCategorySecond();
            healthCategorySecond.setFirstId(healthCategoryFirst.getId());

            List<HealthCategorySecond> healthCategorySecondList = this.healthCategorySecondService.queryListByWhere
                    (healthCategorySecond);

            // 如果一个大类不存在亚类，不把它加入到level中
            if (healthCategorySecondList != null && healthCategorySecondList.size() > 0) {
                result.put(healthCategoryFirst.getName(), healthCategorySecondList);
            }
        });

        return CommonResult.success("查询成功", result);
    }
}
