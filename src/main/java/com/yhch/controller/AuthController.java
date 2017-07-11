package com.yhch.controller;

import com.yhch.bean.CommonResult;
import com.yhch.bean.Constant;
import com.yhch.pojo.User;
import com.yhch.service.PropertyService;
import com.yhch.service.RedisService;
import com.yhch.service.UserService;
import com.yhch.util.MD5Util;
import com.yhch.util.SMSUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Random;


/**
 * 登录、注册和首页跳转
 * Created by zlren on 2017/6/6.
 */
@Controller
@RequestMapping("auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private RedisService redisService;

    /**
     * 发送短信验证码
     *
     * @param params
     * @return
     */
    @RequestMapping(value = "send_sms", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult sendSms(@RequestBody Map<String, String> params) {

        String phone = params.get(Constant.PHONE);

        if (redisService.get(phone) != null) {
            return CommonResult.failure("请1分钟后再试");
        }

        StringBuilder code = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < propertyService.smsCodeLen; i++) {
            // code.append(String.valueOf(random.nextInt(10)));
            code.append(i);
        }

        String smsText = "【医海慈航】您的注册验证码为" + code + "，一分钟内有效";
        logger.info("用户{}： {}", phone, smsText);

        CommonResult result;
        try {
            result = SMSUtil.send(phone, String.valueOf(code), smsText);
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResult.failure("短信发送失败，请重试");
        }

        // 存储在redis中，过期时间为60s
        redisService.setSmSCode(Constant.REDIS_PRE_CODE + phone, String.valueOf(code));

        return result;
    }

    /**
     * 注册
     *
     * @param params
     * @return
     */
    @RequestMapping(value = "register", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult register(@RequestBody Map<String, String> params) {

        // username就是手机号
        String username = params.get(Constant.PHONE);
        String password = params.get(Constant.PASSWORD);
        String phone = params.get(Constant.PHONE);
        String inputCode = params.get(Constant.INPUT_CODE);
        String name = params.get(Constant.NAME);

        logger.info("inputCode = {}", inputCode);

        String code = redisService.get(Constant.REDIS_PRE_CODE + phone);
        if (code == null || !code.equals(inputCode)) {
            return CommonResult.failure("验证码错误");
        }

        if (this.userService.isExist(username)) {
            return CommonResult.failure("用户名已经存在");
        }

        try {
            User user = new User();
            user.setUsername(username);
            user.setPassword(MD5Util.generate(password));
            // user.setPhone(phone);
            user.setRole(Constant.USER_1);
            user.setName(name);
            user.setAvatar("avatar_default.png"); // 默认头像
            this.userService.save(user);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return CommonResult.failure("注册失败");
        }

        return CommonResult.success("注册成功");
    }


    /**
     * 会员登录
     *
     * @param params
     * @return
     */
    @RequestMapping(value = "member/login", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult userLogin(@RequestBody Map<String, String> params) {

        // 得到用户名和密码，用户名就是phone
        String username = params.get(Constant.PHONE);
        String password = params.get(Constant.PASSWORD);

        return this.userService.login(username, password, "member");
    }


    /**
     * 职员登录
     *
     * @param params
     * @return
     */
    @RequestMapping(value = "employee/login", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult employeeLogin(@RequestBody Map<String, String> params) {

        // 得到用户名和密码，用户名就是phone
        String username = params.get(Constant.PHONE);
        String password = params.get(Constant.PASSWORD);

        return this.userService.login(username, password, "employee");
    }


    /**
     * 登录
     *
     * @param params
     * @return
     */
    @RequestMapping(value = "login", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult login(@RequestBody Map<String, String> params) {

        // 得到用户名和密码，用户名就是phone
        String username = params.get(Constant.PHONE);
        String password = params.get(Constant.PASSWORD);

        return this.userService.login(username, password, "null");
    }


    /**
     * 未验证跳转
     *
     * @return
     */
    @RequestMapping(value = "login_denied")
    @ResponseBody
    public CommonResult loginDenied() {
        logger.info("login_denied");
        return CommonResult.failure("请先登录");
    }


    /**
     * 权限拒绝
     *
     * @return
     */
    @RequestMapping(value = "role_denied")
    @ResponseBody
    public CommonResult roleDenied() {
        logger.info("role_denied");
        return CommonResult.failure("无此权限");
    }
}
