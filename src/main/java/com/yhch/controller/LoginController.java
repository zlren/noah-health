package com.yhch.controller;

import com.yhch.bean.ROLES;
import com.yhch.service.MemberService;
import com.yhch.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * LoginController
 */
@Controller
@RequestMapping("/auth")
public class LoginController {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private MemberService memberService;

    /**
     * 显示登录成功后对应该用户等级的首页
     *
     * @return
     * @throws Exception
     */
    @RequestMapping("home")
    public String home() throws Exception {

        Subject subject = SecurityUtils.getSubject();

        if (subject.hasRole(ROLES.ADMIN)) {
            return "home";
        } else if (subject.hasRole(ROLES.USER_1)) {
            return "home";
        } else {
            return null;
        }
    }


    /**
     * 通用页面跳转controller
     *
     * @return
     * @throws Exception
     */
    @RequestMapping("{pageName}")
    public String login(@PathVariable("pageName") String pageName) {

        return pageName;
    }
}
