package com.yhch.service;

import com.github.pagehelper.PageHelper;
import com.yhch.bean.CommonResult;
import com.yhch.bean.Constant;
import com.yhch.bean.Identity;
import com.yhch.pojo.User;
import com.yhch.util.MD5Util;
import com.yhch.util.TokenUtil;
import com.yhch.util.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService extends BaseService<User> {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private PropertyService propertyService;

    /**
     * 检查用户名是否重复
     *
     * @param username
     * @return true 重复（数据库中存在）
     */
    public boolean isExist(String username) {
        User record = new User();
        record.setUsername(username);
        return super.queryOne(record) != null;
    }


    /**
     * 登录验证
     *
     * @param username
     * @param password
     * @param type
     * @return
     */
    public CommonResult login(String username, String password, String type) {

        try {
            Thread.sleep(600);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        logger.info("{} 用户请求登录", username);

        if (!this.isExist(username)) {
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
        User record = new User();
        record.setUsername(username);
        User targetUser = this.queryOne(record);

        if (type.equals("member")) {
            if (!this.checkMember(targetUser.getRole())) {
                return CommonResult.failure("请选择正确的登录入口");
            }
        } else if (type.equals("employee")) {
            if (this.checkMember(targetUser.getRole())) {
                return CommonResult.failure("请选择正确的登录入口");
            }
        } else {
            // 目前的分支
        }

        // 检验密码
        if (!targetUser.getPassword().equals(md5Password)) {
            return CommonResult.failure("密码错误");
        }

        // 生成token
        CommonResult result = this.generateToken(targetUser.getId().toString(),
                propertyService.issuer,
                targetUser.getUsername(),
                targetUser.getRole(),
                "/avatar/" + targetUser.getAvatar(),
                propertyService.tokenDuration,
                propertyService.apiKeySecret);

        ((Identity) result.getContent()).setName(targetUser.getName());

        return result;
    }


    /**
     * 为通过登录验证的用户生成token
     *
     * @param id
     * @param issuer
     * @param username
     * @param role
     * @param avatar
     * @param duration
     * @param apiKeySecret
     * @return
     */
    public CommonResult generateToken(String id, String issuer, String username, String role, String avatar, Long
            duration, String
                                              apiKeySecret) {

        Identity identity = new Identity();
        identity.setId(id);
        identity.setIssuer(issuer);
        identity.setUsername(username);
        identity.setRole(role);
        identity.setDuration(duration);
        identity.setAvatar(avatar);
        String token = TokenUtil.createToken(identity, apiKeySecret);

        // 封装返回前端(除了用户名、角色、时间戳保留，其余消去)
        identity.setToken(token);
        identity.setIssuer(null);
        return CommonResult.success("登录成功", identity);
    }


    /**
     * 条件查询会员
     *
     * @param pageNow
     * @param pageSize
     * @param role
     * @param phone
     * @param name
     * @param type
     * @param identity
     * @return
     */
    public List<User> queryUserList(Integer pageNow, Integer pageSize, String role, String phone, String name, String
            type, Identity identity) {

        Example example = new Example(User.class);
        Example.Criteria criteria = example.createCriteria();

        if (!Validator.checkEmpty(name)) {
            criteria.andLike(Constant.NAME, "%" + name + "%");
        }

        if (!Validator.checkEmpty(phone)) {
            criteria.andLike(Constant.USERNAME, "%" + phone + "%");
        }

        if (!Validator.checkEmpty(role)) {
            criteria.andLike(Constant.ROLE, "%" + role + "%");
        } else {
            if (type.equals(Constant.MEMBER)) {
                // criteria.andLike(Constant.ROLE, "%会员%");
                criteria.andIn("id", this.queryMemberIdSetUnderRole(identity));
            } else { // type.equals("Constant.EMPLOYEE")
                criteria.andNotLike(Constant.ROLE, "%会员%");
                criteria.andIn("id", this.queryStaffIdSetUnderManager(identity));
            }
        }

        PageHelper.startPage(pageNow, pageSize);
        return this.getMapper().selectByExample(example);
    }


    /**
     * 根据顾问部主管的id查找对应的顾问部成员，继而查找顾问对应的member
     *
     * @param adviseMgrId
     * @return
     */
    public List<User> queryMembersByAdviseMgrId(Integer adviseMgrId) {

        User record = new User();
        record.setStaffMgrId(String.valueOf(adviseMgrId));
        record.setRole(Constant.ADVISER);
        List<User> adviserList = this.queryListByWhere(record);

        Set<String> adviserIdSet = new HashSet<>();

        adviserList.forEach(advise -> adviserIdSet.add(String.valueOf(advise.getId())));

        Example example = new Example(User.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andIn("staffId", adviserIdSet);
        return this.getMapper().selectByExample(example);
    }


    /**
     * 根据顾问查找对应的member
     *
     * @param adviserId
     * @return
     */
    public List<User> queryMembersByAdviseId(Integer adviserId) {
        User record = new User();
        record.setStaffId(String.valueOf(adviserId));
        return this.queryListByWhere(record);
    }


    /**
     * 查询所有会员
     *
     * @return
     */
    public List<User> queryAllMembers() {

        Example example = new Example(User.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andLike("role", "%会员%");
        return this.getMapper().selectByExample(example);
    }


    /**
     * 查询旗下的会员
     *
     * @param identity
     * @return
     */
    public List<User> queryMemberListUnderEmployee(Identity identity) {

        // 当前用户的role和id
        String role = identity.getRole();
        String id = identity.getId();

        List<User> users = new ArrayList<>();
        if (role.equals(Constant.ARCHIVE_MANAGER) || role.equals(Constant.ARCHIVER) || role.equals(Constant.ADMIN)) {
            // 档案部员工、主管以及超级管理员，所有的会员
            users = this.queryAllMembers();
        } else if (role.equals(Constant.ADVISE_MANAGER)) {
            users = this.queryMembersByAdviseMgrId(Integer.valueOf(id));
        } else if (role.equals(Constant.ADVISER)) {
            users = this.queryMembersByAdviseId(Integer.valueOf(id));
        }
        return users;
    }

    /**
     * 查询旗下的会员，返回这些会员的id组成的set
     *
     * @param identity
     * @return
     */
    public Set<Integer> queryMemberIdSetUnderRole(Identity identity) {

        Set<Integer> memberSet = new HashSet<>();

        if (this.checkMember(identity.getRole())) {
            memberSet.add(Integer.valueOf(identity.getId()));
        } else {
            List<User> memberList = this.queryMemberListUnderEmployee(identity);
            memberList.forEach(member -> memberSet.add(member.getId()));
            if (memberSet.size() == 0) {
                memberSet.add(-1); // 空的话会出错
            }
        }

        return memberSet;
    }


    /**
     * 查询旗下职员
     *
     * @param identity
     * @return
     */
    public Set<Integer> queryStaffIdSetUnderManager(Identity identity) {

        String role = identity.getRole();
        String id = identity.getId();

        List<User> userList = null;

        if (this.checkManager(role)) {
            User record = new User();
            record.setStaffMgrId(id);
            userList = this.queryListByWhere(record);
        } else if (this.checkAdmin(role)) {
            Example userExample = new Example(User.class);
            Example.Criteria userCriteria = userExample.createCriteria();
            userCriteria.andNotLike("role", "%会员%");
            userList = this.getMapper().selectByExample(userExample);
        }

        Set<Integer> staffSet = new HashSet<>();
        userList.forEach(staff -> staffSet.add(staff.getId()));

        return staffSet;
    }


    /**
     * 一级、二级和三级会员
     *
     * @param role
     * @return true表示是
     */
    public boolean checkMember(String role) {
        return role.equals(Constant.USER_1) || role.equals(Constant.USER_2) || role.equals(Constant.USER_3);
    }

    /**
     * 顾问部员工、档案部员工、财务部员工
     *
     * @param role
     * @return
     */
    public boolean checkStaff(String role) {
        return role.equals(Constant.ADVISER) || role.equals(Constant.ARCHIVER) || role.equals(Constant.FINANCER);
    }

    /**
     * 顾问部主管、档案部主管
     *
     * @param role
     * @return
     */
    public boolean checkManager(String role) {
        return role.equals(Constant.ADVISE_MANAGER) || role.equals(Constant.ARCHIVE_MANAGER);
    }

    /**
     * 系统管理员
     *
     * @param role
     * @return
     */
    public boolean checkAdmin(String role) {
        return role.equals(Constant.ADMIN);
    }

    /**
     * 模糊匹配姓名，查询会员
     *
     * @param userName
     * @return
     */
    public Set<Integer> getMemberIdSetByUserNameLike(String userName) {
        return getIdSetByUserNameLikeAndRole(userName, "会员");
    }


    /**
     * 模糊匹配姓名，查询职员
     *
     * @param userName
     * @return
     */
    public Set<Integer> getEmployeeIdSetByUserNameLike(String userName) {
        return this.getIdSetByUserNameLikeAndRole(userName, "职员");
    }

    /**
     * 根据姓名和角色模糊匹配，将匹配的结果的id组成set返回
     *
     * @param name
     * @param role
     * @return
     */
    public Set<Integer> getIdSetByUserNameLikeAndRole(String name, String role) {

        Example userExample = new Example(User.class);
        Example.Criteria userCriteria = userExample.createCriteria();
        userCriteria.andLike("name", "%" + name + "%");

        if (role.equals("职员")) {
            userCriteria.andNotLike("role", "%会员%");
        } else {
            userCriteria.andLike("role", "%" + role + "%");
        }

        List<User> userList = this.getMapper().selectByExample(userExample);

        Set<Integer> userIdSet = new HashSet<>();
        userList.forEach(user -> userIdSet.add(user.getId()));

        return userIdSet;
    }


    /**
     * 根据档案部主管，查询自己治下的档案部员工的id集合
     *
     * @param archiverMgrId
     * @return
     */
    public Set<Integer> queryArchiverIdSetByArchiveMgrId(Integer archiverMgrId) {

        Example userExample = new Example(User.class);
        Example.Criteria userCriteria = userExample.createCriteria();

        userCriteria.andEqualTo("staffMgrId", archiverMgrId);
        userCriteria.andEqualTo("role", Constant.ARCHIVER);

        List<User> archiverList = this.getMapper().selectByExample(userExample);

        Set<Integer> archiverIdSet = new HashSet<>();
        archiverList.forEach(archiver -> archiverIdSet.add(archiver.getId()));

        return archiverIdSet;
    }


    /**
     * 根据角色返回可用的状态集合
     *
     * @param identity
     * @return
     */
    public Set<String> getStatusSetUnderRole(Identity identity) {

        String role = identity.getRole();
        Set<String> statusSet = new HashSet<>();

        if (role.equals(Constant.ADMIN)) {
            statusSet.add(Constant.WEI_TONG_GUO);
            statusSet.add(Constant.YI_TONG_GUO);
            statusSet.add(Constant.LU_RU_ZHONG);
            statusSet.add(Constant.DAI_SHEN_HE);
            statusSet.add(Constant.SHANG_CHUAN_ZHONG);
        } else if (role.equals(Constant.ARCHIVER)) { // 档案部员工只能查看未通过和录入中的
            statusSet.add(Constant.WEI_TONG_GUO);
            statusSet.add(Constant.LU_RU_ZHONG);
            statusSet.add(Constant.SHANG_CHUAN_ZHONG);
        } else if (role.equals(Constant.ARCHIVE_MANAGER)) { // 档案部主管
            statusSet.add(Constant.DAI_SHEN_HE);
        } else if (role.equals(Constant.ADVISER) || role.equals(Constant.ADVISE_MANAGER) || this.checkMember(role)) {
            statusSet.add(Constant.YI_TONG_GUO);
        }

        return statusSet;
    }

}
