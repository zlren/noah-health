package com.yhch.controller;

import com.yhch.bean.CommonResult;
import com.yhch.bean.Constant;
import com.yhch.bean.Identity;
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

import javax.servlet.http.HttpSession;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Random;


/**
 * 登录、注册和首页跳转
 */
@Controller
@RequestMapping("auth")
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

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

        logger.info("inputCode = {}", inputCode);

        String code = redisService.get(Constant.REDIS_PRE_CODE + phone);
        if (code == null || !code.equals(inputCode)) {
            return CommonResult.failure("验证码错误");
        }

        if (this.userService.isExist(username)) {
            return CommonResult.failure("用户名已经存在");
        }

        try {
            this.userService.register(username, password, phone);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return CommonResult.failure("注册失败");
        }
        return CommonResult.success("注册成功");
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

        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 得到用户名和密码，用户名就是phone
        String username = params.get(Constant.PHONE);
        String password = params.get(Constant.PASSWORD);

        logger.info("{} 用户请求登录", username);

        if (!this.userService.isExist(username)) {
            return CommonResult.failure("用户不存在");
        }

        // 密码加密
        String md5Password;
        try {
            md5Password = MD5Util.generate(password);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return CommonResult.failure("MD5加密失败");
        }

        // 从数据库中取出对应的user
        User user = new User();
        user.setUsername(username);
        User targetUser = this.userService.queryOne(user);

        // 检验密码
        if (!targetUser.getPassword().equals(md5Password)) {
            return CommonResult.failure("密码错误");
        }

        // 生成token
        CommonResult result = userService.generateToken(targetUser.getId().toString(),
                propertyService.issuer,
                targetUser.getUsername(),
                targetUser.getRole(),
                propertyService.tokenDuration,
                propertyService.apiKeySecret);

        return result;
    }

    @RequestMapping(value = "hehe")
    @ResponseBody
    public CommonResult hehe(HttpSession session) {

        logger.info("进入hehe.action");

        Identity identity = (Identity) session.getAttribute("identity");
        logger.info("用户名 = " + identity.getPhone());
        logger.info("角色 = " + identity.getRole());


        return CommonResult.success("已登录", null);
    }


    @RequestMapping(value = "login_denied")
    @ResponseBody
    public CommonResult loginDenied() {
        logger.info("login_denied");
        return CommonResult.failure("请先登录");
    }


    @RequestMapping(value = "role_denied")
    @ResponseBody
    public CommonResult roleDenied() {
        logger.info("role_denied");
        return CommonResult.failure("无此权限");
    }
}
