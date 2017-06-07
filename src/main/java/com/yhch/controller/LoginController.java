package com.yhch.controller;

import com.yhch.bean.CommonResult;
import com.yhch.bean.Constant;
import com.yhch.bean.ROLES;
import com.yhch.pojo.User;
import com.yhch.service.MemberService;
import com.yhch.service.PropertyService;
import com.yhch.service.UserService;
import com.yhch.util.SMSUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.Random;


/**
 * 登录、注册和首页跳转
 */
@Controller
@RequestMapping("/auth")
public class LoginController {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private PropertyService propertyService;

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

    @RequestMapping("send_sms")
    @ResponseBody
    public CommonResult sendSms(@RequestParam("phoneNumber") String phoneNumber, HttpServletRequest
            httpServletRequest) {

        StringBuilder code = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < propertyService.codeLen; i++) {
            code.append(String.valueOf(random.nextInt(10)));
        }

        HttpSession session = httpServletRequest.getSession();
        session.setAttribute(Constant.VALIDATE_PHONE, phoneNumber);
        session.setAttribute(Constant.VALIDATE_PHONE_CODE, code.toString());
        session.setAttribute(Constant.SEND_CODE_TIME, new Date().getTime());
        String smsText = code + "(用户注册验证码，一分钟内有效)[医海慈航]";
        try {
            SMSUtil.send(phoneNumber, smsText);
        } catch (Exception e) {
            e.printStackTrace();
            return new CommonResult("failure", "短信发送失败，请重试");
        }
        return new CommonResult("success", "");
    }

    @RequestMapping("register_check")
    @ResponseBody
    public CommonResult registerValidate(String username, String password, String phoneNumber, String inputCode,
                                         HttpServletRequest httpServletRequest) {
        HttpSession session = httpServletRequest.getSession();
        String code = (String) session.getAttribute(Constant.VALIDATE_PHONE_CODE);
        String phone = (String) session.getAttribute(Constant.VALIDATE_PHONE);
        if (phone.equals(phoneNumber) && code.equalsIgnoreCase(inputCode)) {

            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            // todo 当然user表要改，需要加上手机信息

            return CommonResult.success();
        } else {
            return CommonResult.failure("");
        }
    }


    /**
     * 通用页面跳转controller
     * login / register
     *
     * @return
     * @throws Exception
     */
    @RequestMapping("{pageName}")
    public String login(@PathVariable("pageName") String pageName) {
        return pageName;
    }
}
