package com.yhch.controller;

import com.yhch.service.ResultInputService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by zlren on 2017/6/21.
 */
@RequestMapping("input")
@Controller
public class ResultInputController {

    @Autowired
    private ResultInputService resultInputService;
}
