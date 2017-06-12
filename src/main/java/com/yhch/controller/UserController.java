package com.yhch.controller;

import com.github.pagehelper.PageInfo;
import com.yhch.bean.CommonResult;
import com.yhch.bean.Constant;
import com.yhch.bean.PageResult;
import com.yhch.bean.control.RoleCheck;
import com.yhch.pojo.User;
import com.yhch.service.PropertyService;
import com.yhch.service.UserService;
import com.yhch.util.MD5Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
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

    @Autowired
    private PropertyService propertyService;


    /**
     * 查询用户信息
     *
     * @param params
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public CommonResult searchById(@RequestBody Map<String, Object> params) {
        Integer userId = (Integer) params.get(Constant.ID);
        User user = userService.queryById(userId);
        return CommonResult.success("查询成功", user);
    }


    /**
     * 删除用户信息
     *
     * @param params
     * @return
     */
    @RequestMapping(method = RequestMethod.DELETE)
    @ResponseBody
    public CommonResult deleteById(@RequestBody Map<String, Object> params) {
        Integer userId = (Integer) params.get(Constant.ID);
        userService.deleteById(userId);
        return CommonResult.success("删除成功");
    }


    /**
     * 修改密码
     *
     * @param params
     * @return
     */
    @RequestMapping(value = "password", method = RequestMethod.POST)
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
        // this.userService.changePassword(userId, md5Password);

        User user = this.userService.queryById(userId);
        user.setPassword(md5Password);
        this.userService.update(user);

        return CommonResult.success("密码修改成功");
    }


    /**
     * 修改角色
     *
     * @param params
     * @return
     */
    @RequestMapping(value = "role", method = RequestMethod.POST)
    @RoleCheck(roles = {"ADMIN"})
    public CommonResult changeRole(@RequestBody Map<String, Object> params) {

        // 得到用户名和密码，用户名就是phone
        Integer userId = (Integer) params.get(Constant.ID);
        String role = (String) params.get(Constant.ROLE);

        User user = this.userService.queryById(userId);
        user.setRole(role);

        this.userService.update(user);

        return CommonResult.success("角色修改成功");
    }

    /**
     * 分页查询
     *
     * @param params
     * @return
     */
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public CommonResult searchByList(@RequestBody Map<String, Object> params) {
        Integer pageCurrent = (Integer) params.get(Constant.PAGE_CURRENT);
        PageInfo<User> userPageInfo = this.userService.queryPageListByWhere(pageCurrent, propertyService
                .pageRows, null);
        return CommonResult.success("查询成功", new PageResult(userPageInfo));
    }

}
