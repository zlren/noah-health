package com.yhch.controller;

import com.yhch.bean.CommonData;
import com.yhch.bean.CommonResult;
import com.yhch.bean.Constant;
import com.yhch.bean.Identity;
import com.yhch.pojo.User;
import com.yhch.service.AuthorityService;
import com.yhch.service.MemberService;
import com.yhch.service.PropertyService;
import com.yhch.service.UserService;
import com.yhch.util.MD5Util;
import com.yhch.util.SMSUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
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
    private MemberService memberService;

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private AuthorityService authorityService;


    @RequestMapping("send_sms")
    @ResponseBody
    public CommonResult sendSms(@RequestBody Map<String, String> params) {

        String phone = params.get("phone");

        // 1分钟内无法再次请求验证码，需要检查一下commonData中是否存在此手机号，看时间戳和现在时间的对比
        if (!CommonData.getInstance().sendCheck(phone)) {
            return CommonResult.failure("请1分钟后再试");
        }

        StringBuilder code = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < propertyService.codeLen; i++) {
            code.append(String.valueOf(random.nextInt(10)));
        }

        String smsText = code + "(用户注册验证码，一分钟内有效)[医海慈航]";
        logger.info(smsText);

        CommonResult result;
        try {
            result = SMSUtil.send(phone, String.valueOf(code), smsText);
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResult.failure("短信发送失败，请重试");
        }
        return result;
    }

    /**
     * 注册
     *
     * @param params
     * @return
     */
    @RequestMapping("register")
    @ResponseBody
    public CommonResult register(@RequestBody Map<String, String> params) {

        // username就是手机号
        String username = params.get("phone");
        String password = params.get("password");
        String phoneNumber = params.get("phone");
        String inputCode = params.get("inputCode");

        boolean checkCode = CommonData.getInstance().checkCode(phoneNumber, inputCode);
        if (!checkCode) {
            return CommonResult.failure("验证码错误");
        }

        if (this.userService.isExist(username)) {
            return CommonResult.failure("用户名已经存在");
        }

        try {
            this.userService.register(username, password, phoneNumber);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return CommonResult.failure("注册失败");
        }
        return CommonResult.success();
    }


    /**
     * 登录
     *
     * @param params
     * @return
     */
    @RequestMapping("login")
    @ResponseBody
    public CommonResult login(@RequestBody Map<String, String> params) {

        // 得到用户名和密码，用户名就是phone
        String username = params.get("phone");
        String password = params.get("password");

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
    public CommonResult hehe(@RequestBody Map<String, Object> params, HttpSession session) {

        CommonResult checkResult = authorityService.check(session, Arrays.asList(Constant.ADMIN, Constant
                .ADVISER));
        if (checkResult.getCode().equals(Constant.FAILURE)) {
            return checkResult;
        }

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
}
