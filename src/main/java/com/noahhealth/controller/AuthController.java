package com.noahhealth.controller;

import com.noahhealth.bean.CommonResult;
import com.noahhealth.bean.Constant;
import com.noahhealth.pojo.User;
import com.noahhealth.service.PropertyService;
import com.noahhealth.service.RedisService;
import com.noahhealth.service.UserService;
import com.noahhealth.util.MD5Util;
import com.noahhealth.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Random;


/**
 * 登录、注册和首页跳转
 * Created by zlren on 2017/6/6.
 */
@RestController
@RequestMapping("auth")
@Slf4j
public class AuthController {

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
    public CommonResult sendSms(@RequestBody Map<String, String> params) {

        String phone = params.get(Constant.PHONE);

        User record = new User();
        record.setUsername(phone);
        User user = this.userService.queryOne(record);
        String action = params.get("action");
        if (action.equals("注册") || action.equals("修改手机")) {
            if (user != null) {
                return CommonResult.failure("此号码已经注册");
            }
        } else if (action.equals("找回密码")) {
            if (user == null) {
                return CommonResult.failure("此号码未注册");
            }
        }

        if (redisService.get(phone) != null) {
            return CommonResult.failure("请1分钟后再试");
        }

        StringBuilder code = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < propertyService.smsCodeLen; i++) {
            code.append(String.valueOf(random.nextInt(10)));
        }

        log.info("验证码，手机号：{}，验证码：{}", phone, code);

        CommonResult result;
        try {
            result = CommonResult.success("发送成功，验证码为：" + code, record.getId());
            // result = SMSUtil.send(phone, String.valueOf(code));
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
    public CommonResult register(@RequestBody Map<String, String> params) {

        // username就是手机号
        String username = params.get(Constant.PHONE);
        String password = params.get(Constant.PASSWORD);
        String phone = params.get(Constant.PHONE);
        String inputCode = params.get(Constant.INPUT_CODE);
        String name = params.get(Constant.NAME);

        log.info("inputCode = {}", inputCode);

        String code = redisService.get(Constant.REDIS_PRE_CODE + phone);
        if (code == null) {
            return CommonResult.failure("验证码过期");
        } else if (!code.equals(inputCode)) {
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
            user.setMemberNum(null);
            user.setValid(TimeUtil.getOneYearAfterTime()); // 默认有效期一年

            // 默认顾问和顾问主管
            user.setStaffId(this.propertyService.defaultAdviser);

            this.userService.save(user);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return CommonResult.failure("注册失败");
        }

        return CommonResult.success("注册成功");
    }


    /**
     * 校验验证码
     *
     * @param params
     * @return
     */
    @RequestMapping(value = "check_code", method = RequestMethod.POST)
    public CommonResult checkSMSCode(@RequestBody Map<String, String> params) {

        String inputCode = params.get(Constant.INPUT_CODE);
        String phone = params.get(Constant.PHONE);

        log.info("传过来的验证码是: {}， 手机是：{}", inputCode, phone);

        String code = redisService.get(Constant.REDIS_PRE_CODE + phone);
        if (code == null) {
            return CommonResult.failure("验证码过期");
        } else if (!code.equals(inputCode)) {
            return CommonResult.failure("验证码错误");
        }

        User record = new User();
        record.setUsername(phone);

        return CommonResult.success("验证成功", this.userService.queryOne(record).getId());
    }


    /**
     * 会员登录
     *
     * @param params
     * @return
     */
    @RequestMapping(value = "member/login", method = RequestMethod.POST)
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
    public CommonResult loginDenied() {
        log.info("login_denied");
        return CommonResult.failure("请先登录");
    }


    /**
     * 权限拒绝
     *
     * @return
     */
    @RequestMapping(value = "role_denied")
    public CommonResult roleDenied() {
        log.info("role_denied");
        return CommonResult.failure("无此权限");
    }
}
