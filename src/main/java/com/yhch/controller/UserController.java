package com.yhch.controller;

import com.yhch.pojo.User;
import com.yhch.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * UserController
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/showInfo/{userId}")
    // @RequiresRoles()
    public String showUserInfo(ModelMap modelMap, @PathVariable int userId) {
        User userInfo = userService.queryById(userId);
        modelMap.addAttribute("userInfo", userInfo);
        return "/user/showInfo";
    }

}
