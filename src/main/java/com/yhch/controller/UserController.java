package com.yhch.controller;

import com.github.pagehelper.PageInfo;
import com.yhch.bean.CommonResult;
import com.yhch.bean.Constant;
import com.yhch.bean.PageResult;
import com.yhch.bean.rolecheck.RequiredRoles;
import com.yhch.pojo.User;
import com.yhch.service.UserService;
import com.yhch.util.MD5Util;
import com.yhch.util.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * UserController
 */
@Controller
@RequestMapping("user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;


    /**
     * 查询用户信息
     *
     * @param userId
     * @return
     */
    @RequestMapping(value = "{userId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult queryById(@PathVariable("userId") Integer userId) {

        User user = this.userService.queryById(userId);
        if (user == null) {
            return CommonResult.failure("用户不存在");
        }

        String role = user.getRole();

        if (role.equals(Constant.USER_1) || role.equals(Constant.USER_2) || role.equals
                (Constant.USER_3)) {
            User adviser = this.userService.queryById(Integer.valueOf(user.getAdviserId()));
            user.setAdviseMgrId(this.userService.queryById(Integer.valueOf(adviser.getAdviseMgrId())).getName());
        } else if (role.equals(Constant.ADMIN)) {

        }

        return CommonResult.success("查询成功", user);
    }


    /**
     * 删除用户
     *
     * @param userId
     * @return
     */
    @RequestMapping(value = "{userId}", method = RequestMethod.DELETE)
    @ResponseBody
    @RequiredRoles(roles = {"系统管理员"})
    public CommonResult deleteById(@PathVariable("userId") Integer userId) {

        User user = this.userService.queryById(userId);
        if (user == null) {
            return CommonResult.failure("用户不存在");
        }

        this.userService.deleteById(userId);
        logger.info("删除用户：{}", user.getName());

        return CommonResult.success("删除成功");
    }


    /**
     * 修改一个用户
     *
     * @param userId
     * @param params
     * @return
     */
    @RequestMapping(value = "{userId}", method = RequestMethod.PUT)
    @ResponseBody
    public CommonResult updateById(@PathVariable("userId") Integer userId, @RequestBody Map<String, Object> params) {

        String name = (String) params.get(Constant.NAME);
        String phone = (String) params.get(Constant.PHONE);
        String role = (String) params.get(Constant.ROLE);
        String adviserId = String.valueOf(params.get(Constant.ADVISER_ID));

        User user = new User();
        user.setId(userId);

        if (!Validator.checkEmpty(name)) {
            user.setName(name);
        }

        if (!Validator.checkEmpty(phone)) {
            user.setPhone(phone);
            user.setUsername(phone);
        }

        if (!Validator.checkEmpty(role)) {
            user.setRole(role);
        }

        if (!Validator.checkEmpty(adviserId)) {
            String adviseMgrId = this.userService.queryById(Integer.valueOf(adviserId)).getAdviseMgrId();
            user.setAdviserId(adviserId);
            user.setAdviseMgrId(adviseMgrId);
        }

        this.userService.updateSelective(user);

        return CommonResult.success("修改成功");
    }


    /**
     * 顾问部主管列表
     *
     * @return
     */
    @RequestMapping(value = "manager/list", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult queryManagerList(@RequestBody Map<String, Object> params) {

        String manager = (String) params.get(Constant.MANAGER);

        User mgr = new User();
        mgr.setRole(manager);
        List<User> mgrList = this.userService.queryListByWhere(mgr);

        return CommonResult.success("查询成功", mgrList);
    }


    /**
     * 级联查询顾问部主管和员工
     *
     * @return
     */
    @RequestMapping(value = "advise/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult queryAdviseList() {

        User adviseMgr = new User();
        adviseMgr.setRole(Constant.ADVISE_MANAGER);
        List<User> adviseMgrList = this.userService.queryListByWhere(adviseMgr);

        Map<String, List<User>> result = new HashMap<>();

        adviseMgrList.forEach(adviseMgrTemp -> {

            Integer adviseMgrId = adviseMgrTemp.getId();

            User adviser = new User();
            adviser.setAdviseMgrId(String.valueOf(adviseMgrId));
            adviser.setRole(Constant.ADVISER);
            List<User> adviserList = this.userService.queryListByWhere(adviser);

            result.put(adviseMgrTemp.getName(), adviserList);
        });

        return CommonResult.success("查询成功", result);
    }


    /**
     * 条件分页查询用户
     * 会员member、职员employee
     *
     * @param params
     * @return
     */
    @RequestMapping(value = "{type}/list", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult queryMember(@RequestBody Map<String, Object> params, @PathVariable("type") String type) {

        Integer pageNow = (Integer) params.get(Constant.PAGE_NOW);
        Integer pageSize = (Integer) params.get(Constant.PAGE_SIZE);

        String role = (String) params.get(Constant.ROLE);
        String phone = (String) params.get(Constant.PHONE);
        String name = (String) params.get(Constant.NAME);

        List<User> userList = this.userService.queryUserList(pageNow, pageSize, role, phone, name, type);
        if (type.equals(Constant.MEMBER)) {
            userList.forEach(user -> {
                User adviser = this.userService.queryById(Integer.valueOf(user.getAdviserId()));
                user.setAdviserId(adviser.getName());
                user.setAdviseMgrId(this.userService.queryById(Integer.valueOf(adviser.getAdviseMgrId())).getName());
            });
        }

        logger.info("pageNow: {}, pageSize: {}, role: {}, phone: {}, name: {}", pageNow, pageSize, role, phone, name);

        return CommonResult.success("查询成功", new PageResult(new PageInfo<>(userList)));
    }


    /**
     * 修改密码
     *
     * @param params
     * @return
     */
    @RequestMapping(value = "password", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult changePassword(@RequestBody Map<String, Object> params) {

        // 得到用户名和密码，用户名就是phone
        Integer userId = (Integer) params.get(Constant.ID);
        String password = (String) params.get(Constant.PASSWORD);
        String md5Password;
        try {
            md5Password = MD5Util.generate(password);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return CommonResult.failure("md5加密失败");
        }

        User user = this.userService.queryById(userId);
        user.setPassword(md5Password);
        this.userService.update(user);

        logger.info("{}的密码修改为：{}", user.getName(), password);

        return CommonResult.success("密码修改成功");
    }
}
