package com.yhch.controller;

import com.yhch.bean.CommonResult;
import com.yhch.pojo.User;
import com.yhch.service.UserService;
import com.yhch.util.MD5Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * UserController
 */
@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @RequestMapping("/showInfo/{userId}")
    public String showUserInfo(ModelMap modelMap, @PathVariable int userId) {
        User userInfo = userService.queryById(userId);
        modelMap.addAttribute("userInfo", userInfo);
        return "/user/showInfo";
    }

    /**
     * 修改密码
     *
     * @param params
     * @return
     */
    @RequestMapping("change_password")
    public CommonResult changePassword(@RequestBody Map<String, String> params) {
        // 得到用户名和密码，用户名就是phone
        String username = params.get("phone");
        String md5Password;
        try {
            md5Password = MD5Util.generate(params.get("password"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return CommonResult.failure("md5加密失败");
        }

        this.userService.changePassword(username, md5Password);
        return CommonResult.success();
    }

}
