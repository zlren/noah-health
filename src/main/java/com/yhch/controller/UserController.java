package com.yhch.controller;

import com.github.pagehelper.PageInfo;
import com.yhch.bean.CommonResult;
import com.yhch.bean.Constant;
import com.yhch.bean.Identity;
import com.yhch.bean.PageResult;
import com.yhch.bean.rolecheck.RequiredRoles;
import com.yhch.bean.user.UserExtend;
import com.yhch.pojo.User;
import com.yhch.service.PropertyService;
import com.yhch.service.RedisService;
import com.yhch.service.UserService;
import com.yhch.util.FileUtil;
import com.yhch.util.MD5Util;
import com.yhch.util.Validator;
import org.apache.commons.fileupload.util.Streams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.FileOutputStream;
import java.io.IOException;
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

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private RedisService redisService;


    /**
     * 添加员工或者主管
     *
     * @param params
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public CommonResult addEmployee(@RequestBody Map<String, Object> params) {

        String name = (String) params.get(Constant.NAME);
        String phone = (String) params.get(Constant.PHONE);
        String memberNum = (String) params.get("memberNum");
        String role = (String) params.get(Constant.ROLE);
        Integer staffMgrId = (Integer) params.get(Constant.STAFF_MGR_ID);

        User user = new User();

        if (Validator.checkEmpty(name) || Validator.checkEmpty(phone) || Validator.checkEmpty(role)) {
            return CommonResult.failure("添加失败，信息不完整");
        } else {
            user.setName(name);
            user.setUsername(phone);
            user.setRole(role);
        }

        if (!Validator.checkEmpty(memberNum)) {
            user.setMemberNum(memberNum);
        }

        // 是员工但并不是财务部员工，所以需要主管
        if (this.userService.checkStaff(role)) {
            if (!role.equals(Constant.FINANCER)) {
                if (staffMgrId == null) {
                    return CommonResult.failure("添加失败，信息不完整");
                } else {
                    user.setStaffMgrId(staffMgrId);
                }
            }
        }

        User record = new User();
        record.setUsername(phone);
        if (this.userService.queryOne(record) != null) {
            return CommonResult.failure("手机号已注册");
        }

        try {
            user.setPassword(MD5Util.generate(propertyService.defaultPassword));
            user.setAvatar("avatar_default.png"); // 默认头像
            this.userService.save(user);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return CommonResult.failure("添加失败，md5生成错误");
        }

        return CommonResult.success("添加成功");
    }


    /**
     * 修改别的用户的信息
     *
     * @param params
     * @return
     */
    @RequestMapping(method = RequestMethod.PUT)
    @ResponseBody
    public CommonResult updateOtherUser(@RequestBody Map<String, Object> params) {

        Integer userId = (Integer) params.get("userId");
        // 修改别的用户的时候不能修改name和phone
        String name = (String) params.get(Constant.NAME);
        // String phone = (String) params.get(Constant.PHONE);
        String role = (String) params.get(Constant.ROLE);
        Integer adviserId = (Integer) params.get(Constant.STAFF_ID);
        Integer staffMgrId = (Integer) params.get(Constant.STAFF_MGR_ID);
        String memberNum = (String) params.get("memberNum");

        // 未修改的user
        User user = this.userService.queryById(userId);

        if (!Validator.checkEmpty(name)) {
            user.setName(name);
        }

        if (!Validator.checkEmpty(memberNum)) {
            user.setMemberNum(memberNum);
        }

        // role
        if (!Validator.checkEmpty(role)) {
            if (this.userService.checkMember(user.getRole()) && this.userService.checkMember(role)) {
                // 以前是会员，现在也是会员

            } else if (
                    this.userService.checkStaff(user.getRole()) // 以前是员工
                            &&
                            (this.userService.checkStaff(role) || this.userService.checkAdmin(role)) // 现在是员工或者admin
                            &&
                            !user.getRole().equals(role)) { // 并且是不同的员工
                // 从员工改为（不同的员工或者admin）

                // 只有user的staffId不为null，表示的是顾问
                // 之前是顾问员工，现在是财务或者档案员工，如果存在以此顾问员工为顾问的会员，则无法修改
                User record = new User();
                record.setStaffId(userId);

                List<User> adviserList = this.userService.queryListByWhere(record);
                if (adviserList != null && adviserList.size() > 0) {
                    // 存在以他为顾问的会员
                    return CommonResult.failure("修改失败：存在以此用户为顾问的会员");
                }

                // 如果改为了财务或者admin，那么所属主管为null
                if (role.equals(Constant.FINANCER) || role.equals(Constant.ADMIN)) {
                    user.setStaffMgrId(null);
                }

            } else if (this.userService.checkManager(user.getRole()) &&
                    (this.userService.checkManager(role) || this.userService.checkAdmin(role)) &&
                    !user.getRole().equals(role)) {
                // 从主管改成（不同的主管或者admin）

                User record = new User();
                record.setStaffMgrId(userId);

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
                    record.setStaffId(userId);

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
                record.setStaffMgrId(userId);

                List<User> staffList = this.userService.queryListByWhere(record);
                if (staffList != null && staffList.size() > 0) {
                    // 存在以他为主管的员工
                    return CommonResult.failure("修改失败：存在以此用户为主管的员工");
                }

                // 除财务员工外，顾问员工和档案员工都需要主管字段，所以staffMgrId不为空
                if (!role.equals(Constant.FINANCER)) {
                    if (staffMgrId != null) {
                        user.setStaffMgrId(null);
                    } else {
                        return CommonResult.failure("未指定主管");
                    }
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
            Integer adviseMgrId = this.userService.queryById(adviserId).getStaffMgrId();
            user.setStaffId(adviserId);
            user.setStaffMgrId(adviseMgrId);
        }

        if (staffMgrId != null) {
            user.setStaffMgrId(staffMgrId);
        }

        this.userService.update(user);

        return CommonResult.success("修改成功");
    }


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

        UserExtend userExtend = this.userService.extendFromUser(user);

        // String role = user.getRole();
        //
        // if (role.equals(Constant.USER_1) || role.equals(Constant.USER_2) || role.equals
        //         (Constant.USER_3)) {
        //     // User adviser = this.userService.queryById(Integer.valueOf(user.getStaffId()));
        //     // user.setStaffMgrId(this.userService.queryById(adviser.getStaffMgrId()).getName());
        // } else if (role.equals(Constant.ADVISER) || role.equals(Constant.ARCHIVER)) {
        //     // user.setStaffMgrId(this.userService.queryById(Integer.valueOf(user.getStaffMgrId())).getName());
        // }

        return CommonResult.success("查询成功", userExtend);
    }


    /**
     * 删除用户
     * role改为已删除，username加上_deleted的后缀
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

        // this.userService.deleteById(userId);
        user.setRole("已删除");
        user.setUsername(user.getUsername() + "_deleted");
        this.userService.update(user);

        logger.info("删除用户：{}", user.getName());

        return CommonResult.success("删除成功");
    }


    /**
     * 用户自己修改自己
     *
     * @param userId
     * @param params
     * @return
     */
    @RequestMapping(value = "{userId}", method = RequestMethod.PUT)
    @ResponseBody
    public CommonResult updateById(@PathVariable("userId") Integer userId, @RequestBody Map<String, Object> params) {

        // 自己可以修改自己的name和phone
        String name = (String) params.get(Constant.NAME);
        String phone = (String) params.get(Constant.PHONE);

        // 未修改的user
        User user = this.userService.queryById(userId);

        if (!Validator.checkEmpty(name)) {
            user.setName(name);
        }

        if (!Validator.checkEmpty(phone)) {

            User record = new User();
            record.setUsername(phone);
            try {
                if (this.userService.queryOne(record) != null) {
                    return CommonResult.failure("此手机号已注册");
                }
            } catch (Exception ignored) {
                return CommonResult.failure("此手机号已注册");
            }

            String inputCode = (String) params.get(Constant.INPUT_CODE);

            String code = this.redisService.get(Constant.REDIS_PRE_CODE + phone);
            if (code == null) {
                return CommonResult.failure("验证码过期");
            } else if (!code.equals(inputCode)) {
                return CommonResult.failure("验证码错误");
            }

            user.setUsername(phone);
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
            adviser.setStaffMgrId(adviseMgrId);
            adviser.setRole(Constant.ADVISER);
            List<User> adviserList = this.userService.queryListByWhere(adviser);

            // 只返回有员工的顾问主管和员工级联关系
            if (adviserList != null && adviserList.size() > 0) {
                result.put(adviseMgrTemp.getName(), adviserList);
            }
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
    public CommonResult queryMember(@RequestBody Map<String, Object> params, @PathVariable("type") String type,
                                    HttpSession session) {

        Integer pageNow = (Integer) params.get(Constant.PAGE_NOW);
        Integer pageSize = (Integer) params.get(Constant.PAGE_SIZE);

        String role = (String) params.get(Constant.ROLE);
        String phone = (String) params.get(Constant.PHONE);
        String name = (String) params.get(Constant.NAME);

        Identity identity = (Identity) session.getAttribute(Constant.IDENTITY);

        List<User> userList = this.userService.queryUserList(pageNow, pageSize, role, phone, name, type, identity);
        PageResult pageResult = new PageResult(new PageInfo<>(userList));

        // if (type.equals(Constant.MEMBER)) {
        //     userList.forEach(user -> {
        //         User adviser = this.userService.queryById(Integer.valueOf(user.getStaffId()));
        //         user.setStaffId(adviser.getName());
        //         user.setStaffMgrId(this.userService.queryById(Integer.valueOf(adviser.getStaffMgrId())).getName());
        //     });
        // } else if (type.equals(Constant.EMPLOYEE)) {
        //     userList.forEach(user -> {
        //         if (user.getRole().equals(Constant.ADVISER) || user.getRole().equals(Constant.ARCHIVER)) {
        //             user.setStaffMgrId(this.userService.queryById(Integer.valueOf(user.getStaffMgrId())).getName());
        //         }
        //     });
        // }

        List<UserExtend> userExtendList = this.userService.extendFromUser(userList);
        pageResult.setData(userExtendList);

        logger.info("pageNow: {}, pageSize: {}, role: {}, phone: {}, name: {}", pageNow, pageSize, role, phone, name);

        return CommonResult.success("查询成功", pageResult);
    }


    /**
     * 修改密码
     *
     * @param params
     * @return
     */
    @RequestMapping(value = "password/{userId}", method = RequestMethod.PUT)
    @ResponseBody
    public CommonResult changePassword(@RequestBody Map<String, Object> params, @PathVariable("userId") Integer
            userId) {

        String oldPassword = (String) params.get("oldPassword");
        String newPassword = (String) params.get("newPassword");

        User user = this.userService.queryById(userId);

        // 找回密码的时候没有oldPassword
        if (!Validator.checkEmpty(oldPassword)) {
            String oldPasswordMD5;
            try {
                oldPasswordMD5 = MD5Util.generate(oldPassword);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return CommonResult.failure("md5加密失败！");
            }

            if (!oldPasswordMD5.equals(user.getPassword())) {
                return CommonResult.failure("修改失败，原密码输入错误");
            }
        }

        String newPasswordMD5;
        try {
            newPasswordMD5 = MD5Util.generate(newPassword);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return CommonResult.failure("md5加密失败");
        }

        user.setPassword(newPasswordMD5);
        this.userService.update(user);

        return CommonResult.success("密码修改成功");
    }


    /**
     * 修改用户头像
     *
     * @param file
     * @param id
     * @return
     */
    @RequestMapping(value = "avatar", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult uploadAvatar(@RequestParam("file") MultipartFile file, Integer id) {

        User user = this.userService.queryById(id);
        if (user == null) {
            return CommonResult.failure("上传失败，用户不存在");
        }

        String fileName;
        if (!file.isEmpty()) {

            fileName = id + "." + FileUtil.getExtensionName(file.getOriginalFilename());

            try {
                Streams.copy(file.getInputStream(), new FileOutputStream(this.propertyService.filePath + "avatar/" +
                        fileName), true);
            } catch (IOException e) {
                e.printStackTrace();
                return CommonResult.failure("头像上传失败");
            }

            user.setAvatar(fileName);
            this.userService.update(user);
        } else {
            return CommonResult.failure("头像上传失败");
        }

        return CommonResult.success("头像上传成功", "/avatar/" + fileName);
    }
}
