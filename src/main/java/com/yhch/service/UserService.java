package com.yhch.service;

import com.github.pagehelper.PageHelper;
import com.yhch.bean.CommonResult;
import com.yhch.bean.Constant;
import com.yhch.bean.Identity;
import com.yhch.bean.user.UserExtend;
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
import java.util.*;

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
     * 用户是否在有效期内
     *
     * @param user
     * @return true表示有效
     */
    public boolean checkValid(User user) {
        return !this.checkMember(user.getRole()) || new Date().before(user.getValid());
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

        {
            User record = new User();
            record.setUsername(username);
            User user = this.queryOne(record);

            if (user == null) {
                return CommonResult.failure("登录失败：用户不存在");
            }

            if (!this.checkValid(user)) {
                return CommonResult.failure("登录失败：过期无效的用户");
            }
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
            memberNum, String type, Identity identity) {

        Example example = new Example(User.class);
        Example.Criteria criteria = example.createCriteria();

        example.setOrderByClause("field(role,'三级会员','二级会员','一级会员','系统管理员','档案部主管','顾问部主管','档案部员工','顾问部员工', '财务部员工'), " +
                "member_num desc");

        if (!Validator.checkEmpty(name)) {
            criteria.andLike(Constant.NAME, "%" + name + "%");
        }

        if (!Validator.checkEmpty(phone)) {
            criteria.andLike(Constant.USERNAME, "%" + phone + "%");
        }

        if (!Validator.checkEmpty(memberNum)) {
            criteria.andLike("memberNum", "%" + memberNum + "%");
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
        record.setStaffMgrId(adviseMgrId);
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
        record.setStaffId(adviserId);
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
            // 档案部员工、档案部主管以及超级管理员，所有的会员
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
        }

        if (memberSet.size() == 0) {
            memberSet.add(-1); // 空的话会出错
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
            record.setStaffMgrId(Integer.valueOf(id));
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
     * 档案部主管
     *
     * @param role
     * @return
     */
    public boolean checkArchiverManager(String role) {
        return role.equals(Constant.ARCHIVE_MANAGER);
    }

    /**
     * 档案部员工
     *
     * @param role
     * @return
     */
    public boolean checkArchiver(String role) {
        return role.equals(Constant.ARCHIVER);
    }

    /**
     * 顾问部主管
     *
     * @param role
     * @return
     */
    public boolean checkAdviseManager(String role) {
        return role.equals(Constant.ADVISE_MANAGER);
    }

    /**
     * 顾问
     *
     * @param role
     * @return
     */
    public boolean checkAdviser(String role) {
        return role.equals(Constant.ADVISER);
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

        // 结果为空的话查询会出错
        if (userIdSet.size() == 0) {
            userIdSet.add(-1);
        }

        return userIdSet;
    }


    /**
     * 根据档案部主管，查询自己治下的档案部员工的id集合
     *
     * @param archiverMgrId
     * @return
     */
    public Set<Integer> queryArchiverIdSetByArchiveMgrId(String archiverMgrId) {

        Example userExample = new Example(User.class);
        Example.Criteria userCriteria = userExample.createCriteria();

        userCriteria.andEqualTo("staffMgrId", archiverMgrId);
        userCriteria.andEqualTo("role", Constant.ARCHIVER);

        List<User> archiverList = this.getMapper().selectByExample(userExample);

        Set<Integer> archiverIdSet = new HashSet<>();
        archiverList.forEach(archiver -> archiverIdSet.add(archiver.getId()));

        if (archiverIdSet.size() == 0) {
            archiverIdSet.add(-1);
        }

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

        if (role.equals(Constant.ADMIN)) { // 系统管理员
            statusSet.add(Constant.WEI_TONG_GUO);
            statusSet.add(Constant.YI_TONG_GUO);
            statusSet.add(Constant.LU_RU_ZHONG);
            statusSet.add(Constant.DAI_SHEN_HE);
            statusSet.add(Constant.SHANG_CHUAN_ZHONG);
        } else if (role.equals(Constant.ARCHIVER)) { // 档案部员工只能查看未通过和录入中的
            statusSet.add(Constant.WEI_TONG_GUO);
            statusSet.add(Constant.LU_RU_ZHONG);
            statusSet.add(Constant.SHANG_CHUAN_ZHONG);
            statusSet.add(Constant.DAI_SHEN_HE);
        } else if (role.equals(Constant.ARCHIVE_MANAGER)) { // 档案部主管
            statusSet.add(Constant.DAI_SHEN_HE);
        } else if (role.equals(Constant.ADVISER) || role.equals(Constant.ADVISE_MANAGER) || this.checkMember(role)) {
            statusSet.add(Constant.YI_TONG_GUO);
        }

        return statusSet;
    }


    /**
     * 根据memberNum模糊匹配，返回所有的member的id组成的set
     *
     * @param memberNum
     * @return
     */
    public Set<Integer> getMemberIdSetByMemberNumLike(String memberNum) {

        Example userExample = new Example(User.class);
        Example.Criteria userCriteria = userExample.createCriteria();

        userCriteria.andLike("memberNum", "%" + memberNum + "%");
        userCriteria.andLike("role", "%会员%");

        List<User> memberList = this.getMapper().selectByExample(userExample);

        Set<Integer> memberIdSet = new HashSet<>();
        memberList.forEach(user -> memberIdSet.add(user.getId()));

        // 结果为空的话查询会出错
        if (memberIdSet.size() == 0) {
            memberIdSet.add(-1);
        }

        return memberIdSet;
    }


    /**
     * 同时根据会员姓名和会员编号去匹配
     *
     * @param name
     * @param memberNum
     * @return
     */
    public Set<Integer> getMemberIdSetByNameAndMemberNumLike(String name, String memberNum) {
        if (name == null) {
            name = "";
        }
        if (memberNum == null) {
            memberNum = "";
        }

        Set<Integer> nameLike = this.getMemberIdSetByUserNameLike(name);
        nameLike.retainAll(this.getMemberIdSetByMemberNumLike(memberNum));
        return nameLike;
    }


    /**
     * 拓展user，补全
     *
     * @param userList
     * @return
     */
    public List<UserExtend> extendFromUser(List<User> userList) {
        List<UserExtend> userExtendList = new ArrayList<>();
        // 过滤掉过期用户
        userList.stream().filter(this::checkValid).forEach(user -> userExtendList.add(extendFromUser(user)));
        return userExtendList;
    }


    public UserExtend extendFromUser(User user) {

        String staffName = null;
        String staffMgrName = null;

        user.setPassword(null); // 防止md5值外泄

        if (this.checkMember(user.getRole())) {
            if (user.getStaffId() != null) {
                staffName = this.queryById(user.getStaffId()).getName();
                staffMgrName = this.queryById(this.queryById(user.getStaffId()).getStaffMgrId()).getName();
            } else {
                staffName = "<未设置顾问>";
            }
        } else if (user.getRole().equals(Constant.ARCHIVER) || user.getRole().equals(Constant.ADVISER)) {
            staffMgrName = this.queryById(user.getStaffMgrId()).getName();
        }

        return new UserExtend(user, staffName, staffMgrName);
    }
}
