package com.yhch.controller;

import com.yhch.bean.CommonResult;
import com.yhch.service.CategorySecondService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by zlren on 2017/6/22.
 */
@Controller
@RequestMapping("test")
public class TestController {

    @Autowired
    private CategorySecondService categorySecondService;

    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public CommonResult del(@PathVariable("id") Integer id) {
        this.categorySecondService.queryById(1);
        return null;
    }

}
