package com.yhch.controller;

import com.alibaba.fastjson.JSONObject;
import com.yhch.bean.CommonResult;
import com.yhch.bean.Constant;
import com.yhch.interceptor.TokenCertifyInterceptor;
import com.yhch.pojo.Identity;
import com.yhch.pojo.User;
import com.yhch.service.MemberService;
import com.yhch.service.PropertyService;
import com.yhch.service.UserService;
import com.yhch.util.MD5Util;
import com.yhch.util.SMSUtil;
import com.yhch.util.TokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sun.security.provider.MD5;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


/**
 * 登录、注册和首页跳转
 */
@Controller
@RequestMapping("/auth")
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private PropertyService propertyService;


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
            return new CommonResult("failure", "短信发送失败，请重试", null);
        }
        return new CommonResult("success", "", null);
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

            return CommonResult.success("注册成功", null);
        } else {
            return CommonResult.failure("注册失败");
        }
    }

    /**
     * 通用页面跳转controller
     * login / register
     *
     * @return
     * @throws Exception
     */

    @RequestMapping(value = "login")
    @ResponseBody
    public CommonResult login(@RequestBody Map<String, Object> params) {

        logger.info("进入login.action");

        String username = (String) params.get("username");
        String password = (String) params.get("password");

        //1.验证用户名与密码
        CommonResult result = userService.loginValidate(username, password);
        if(result.getCode() != Constant.SUCCESS) return result;

        //2.生成token
        User legalUser = (User) result.getContent(); //取得已通过验证的该用户
        result = userService.generateToken(legalUser.getId().toString(),
                                            propertyService.issuer,
                                            legalUser.getUsername(),
                                            legalUser.getRole(),
                                            propertyService.tokenDuration,
                                            propertyService.apiKeySecret);

        return result;

    }

    @RequestMapping(value = "hehe")
    @ResponseBody
    public CommonResult hehe(@RequestBody Map<String, Object> params, HttpSession session) {

        logger.info("进入hehe.action");


        Identity identity = (Identity) session.getAttribute("identity");
        logger.info("用户名=" + identity.getUsername());
        logger.info("角色=" + identity.getRole());



        return CommonResult.success("已登录", null);

    }




    @RequestMapping(value = "enterPage")
    @ResponseBody
    public CommonResult enterPage() {

        logger.info("进入enterPage");
        return CommonResult.success("成功进入该页面", null);

    }



    @RequestMapping(value = "loginDenied")
    @ResponseBody
    public CommonResult loginDenied() {


        logger.info("进入loginDenied");
        return CommonResult.failure("请先登录");

    }


    @RequestMapping(value = "roleDenied")
    @ResponseBody
    public CommonResult roleDenied() {


        logger.info("进入roleDenied");
        return CommonResult.failure("权限不够");

    }





//    public @ResponseBody Integer addUser(@RequestBody String userString, HttpSession session) {
//        JSONObject userJson = (JSONObject) JSONObject.parse(userString);
//
//        User curUser = (User) session.getAttribute("curUser");
//
//        User newUser = new User();
//
//        newUser.setNUM((String) userJson.get("NUM"));
//        newUser.setUSERNAME((String) userJson.get("USERNAME"));

}
