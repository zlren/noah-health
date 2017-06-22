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
            User adviser = this.userService.queryById(Integer.valueOf(user.getStaffId()));
            user.setStaffMgrId(this.userService.queryById(Integer.valueOf(adviser.getStaffMgrId())).getName());
        } else if (role.equals(Constant.ADVISER) || role.equals(Constant.ARCHIVER)) {
            // user.setStaffMgrId(this.userService.queryById(Integer.valueOf(user.getStaffMgrId())).getName());
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
        Integer adviserId = (Integer) params.get(Constant.STAFF_ID);
        Integer staffMgrId = (Integer) params.get(Constant.STAFF_MGR_ID);

        // 未修改的user
        User user = this.userService.queryById(userId);

        if (!Validator.checkEmpty(name)) {
            user.setName(name);
        }

        if (!Validator.checkEmpty(phone)) {
            user.setPhone(phone);
            user.setUsername(phone);
        }

        // role
        if (!Validator.checkEmpty(role)) {
            if (this.userService.checkMember(user.getRole()) && this.userService.checkMember(role)) {
                // 以前是会员，现在也是会员

            } else if (this.userService.checkStaff(user.getRole()) &&
                    (this.userService.checkStaff(role) || this.userService.checkAdmin(role)) &&
                    !user.getRole().equals(role)) {
                // 从员工改为（不同的员工或者admin）
                // 之前是顾问员工，现在是财务或者档案员工，如果存在以此顾问员工为顾问的会员，则无法修改
                User record = new User();
                record.setStaffId(String.valueOf(userId));

                List<User> adviserList = this.userService.queryListByWhere(record);
                if (adviserList != null && adviserList.size() > 0) {
                    // 存在以他为顾问的会员
                    return CommonResult.failure("修改失败：存在以此用户为顾问的会员");
                }

            } else if (this.userService.checkManager(user.getRole()) &&
                    (this.userService.checkManager(role) || this.userService.checkAdmin(role)) &&
                    !user.getRole().equals(role)) {
                // 从主管改成（不同的主管或者admin）

                User record = new User();
                record.setStaffMgrId(String.valueOf(userId));

                List<User> staffList = this.userService.queryListByWhere(record);
                if (staffList != null && staffList.size() > 0) {
                    // 存在以他为顾问的会员
                    return CommonResult.failure("修改失败：存在以此用户为主管的员工");
                }

            } else if (this.userService.checkStaff(user.getRole()) && this.userService.checkManager(role)) {
                // 从员工改为主管

                logger.info("从员工改为主管");

                // 以前是顾问部员工
                // 如果存在以它为会员的顾问，则不允许修改
                if (user.getRole().equals(Constant.ADVISER)) {

                    User record = new User();
                    record.setStaffId(String.valueOf(userId));

                    List<User> memberList = this.userService.queryListByWhere(record);
                    if (memberList != null && memberList.size() > 0) {
                        // 存在以他为顾问的会员
                        return CommonResult.failure("修改失败：存在以此用户为顾问的会员");
                    }
                }

                user.setStaffMgrId(null);

            } else if (this.userService.checkManager(user.getRole()) && this.userService.checkStaff(role)) {
                // 从主管改为员工

                // 存在以此主管为主管的员工，则不允许修改
                User record = new User();
                record.setStaffMgrId(String.valueOf(userId));

                List<User> staffList = this.userService.queryListByWhere(record);
                if (staffList != null && staffList.size() > 0) {
                    // 存在以他为主管的员工
                    return CommonResult.failure("修改失败：存在以此用户为主管的员工");
                }

                // 员工需要主管字段，所以staffMgrId不为空
                if (staffMgrId != null) {
                    user.setStaffMgrId(null);
                } else {
                    return CommonResult.failure("未指定主管");
                }

            } else if (user.getRole().equals(Constant.ADMIN) && !role.equals(Constant.ADMIN)) {
                // 以前是admin现在不是了
                // 必须保证系统中永远存在ADMIN
                User record = new User();
                record.setRole(Constant.ADMIN);

                List<User> adminList = this.userService.queryListByWhere(record);
                if (adminList != null && adminList.size() >= 2) {
                    // 可以修改

                } else {
                    return CommonResult.failure("修改失败：系统中必须存在至少一个系统管理员");
                }
            }

            user.setRole(role);
        }

        // 只有会员的staff_id不为null
        if (adviserId != null) {
            String adviseMgrId = this.userService.queryById(adviserId).getStaffMgrId();
            user.setStaffId(String.valueOf(adviserId));
            user.setStaffMgrId(adviseMgrId);
        }

        if (staffMgrId != null) {
            user.setStaffMgrId(String.valueOf(staffMgrId));
        }

        this.userService.update(user);

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
            adviser.setStaffMgrId(String.valueOf(adviseMgrId));
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
                User adviser = this.userService.queryById(Integer.valueOf(user.getStaffId()));
                user.setStaffId(adviser.getName());
                user.setStaffMgrId(this.userService.queryById(Integer.valueOf(adviser.getStaffMgrId())).getName());
            });
        } else if (type.equals(Constant.EMPLOYEE)) {
            userList.forEach(user -> {
                if (user.getRole().equals(Constant.ADVISER) || user.getRole().equals(Constant.ARCHIVER)) {
                    user.setStaffMgrId(this.userService.queryById(Integer.valueOf(user.getStaffMgrId())).getName());
                }
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
