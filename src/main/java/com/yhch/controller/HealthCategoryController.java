package com.yhch.controller;

import com.yhch.service.HealthCategoryFirstService;
import com.yhch.service.HealthCategorySecondService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by zlren on 17/7/19.
 */
@RequestMapping("health_category")
@Controller
public class HealthCategoryController {

    @Autowired
    private HealthCategoryFirstService healthCategoryFirstService;

    @Autowired
    private HealthCategorySecondService healthCategorySecondService;

}
