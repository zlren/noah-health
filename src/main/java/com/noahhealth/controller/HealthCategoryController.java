package com.noahhealth.controller;

import com.noahhealth.bean.CommonResult;
import com.noahhealth.pojo.HealthCategoryFirst;
import com.noahhealth.pojo.HealthCategorySecond;
import com.noahhealth.service.HealthCategoryFirstService;
import com.noahhealth.service.HealthCategorySecondService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zlren on 17/7/19.
 */
@RequestMapping("health_category")
@RestController
@Slf4j
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

        Example example = new Example(HealthCategoryFirst.class);
        example.orderBy("id asc");

        List<HealthCategoryFirst> healthCategoryFirstList = this.healthCategoryFirstService.getMapper().selectByExample
                (example);


        Map<String, List<HealthCategorySecond>> result = new LinkedHashMap<>();

        // 用for是保证顺序
        for (int i = 0; i < healthCategoryFirstList.size(); i++) {

            Integer firstId = healthCategoryFirstList.get(i).getId();

            HealthCategorySecond second = new HealthCategorySecond();
            second.setFirstId(firstId);
            List<HealthCategorySecond> healthCategorySecondList = this.healthCategorySecondService.queryListByWhere
                    (second);

            // 只返回有亚类的健康摘要大类
            if (healthCategorySecondList != null && healthCategorySecondList.size() > 0) {
                result.put(healthCategoryFirstList.get(i).getName(), healthCategorySecondList);
            }
        }

        return CommonResult.success("查询成功", result);
    }
}
