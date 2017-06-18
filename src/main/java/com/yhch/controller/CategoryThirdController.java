package com.yhch.controller;

import com.yhch.service.CategoryThirdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by zlren on 2017/6/14.
 */
@Controller
@RequestMapping("third")
public class CategoryThirdController {

    @Autowired
    private CategoryThirdService categoryThirdService;




}
