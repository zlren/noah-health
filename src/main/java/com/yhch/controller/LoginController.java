package com.yhch.controller;

import com.alibaba.fastjson.JSONObject;
import com.yhch.bean.CommonResult;
import com.yhch.bean.Constant;
import com.yhch.pojo.User;
import com.yhch.service.MemberService;
import com.yhch.service.PropertyService;
import com.yhch.service.UserService;
import com.yhch.util.SMSUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(LoginController.class);

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

    @RequestMapping(value = "login")
    @ResponseBody
    public Map<String, Object> login(@RequestBody String jsonString) throws UnsupportedEncodingException {
        jsonString = URLDecoder.decode(jsonString, "utf-8").split("=")[0];
        logger.info(jsonString);

        JSONObject userJson = (JSONObject) JSONObject.parse(jsonString);
        logger.info(userJson.getString("username"));
        logger.info(userJson.getString("password"));


        //验证用户名与密码是否有效
        System.out.println("进入用户名与密码认证action");



        Map<String, Object> map = new HashMap<>();
        map.put("token", false);
        return map;

    }

    @RequestMapping(value = "hehe")
    @ResponseBody
    public Map<String, Object> hehe(@RequestBody String jsonString) throws UnsupportedEncodingException {
//        jsonString = URLDecoder.decode(jsonString, "utf-8").split("=")[0];
//        logger.info(jsonString);
//
//        JSONObject userJson = (JSONObject) JSONObject.parse(jsonString);
//        logger.info(userJson.getString("username"));
//        logger.info(userJson.getString("password"));
//
//
//        //验证用户名与密码是否有效
        System.out.println("进入hehe action");



        Map<String, Object> map = new HashMap<>();
        map.put("token", false);
        return map;

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
