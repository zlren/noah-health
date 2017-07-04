package com.yhch.service;

import com.github.pagehelper.PageHelper;
import com.yhch.bean.CommonResult;
import com.yhch.bean.Constant;
import com.yhch.bean.Identity;
import com.yhch.pojo.User;
import com.yhch.util.TokenUtil;
import com.yhch.util.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService extends BaseService<User> {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

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
     * 为通过登录验证的用户生成token
     *
     * @param id
     * @param issuer
     * @param phone
     * @param role
     * @param duration
     * @param apiKeySecret
     * @return
     */
    public CommonResult generateToken(String id, String issuer, String phone, String role, Long duration, String
            apiKeySecret) {

        Identity identity = new Identity();
        identity.setId(id);
        identity.setIssuer(issuer);
        identity.setPhone(phone);
        identity.setRole(role);
        identity.setDuration(duration);
        String token = TokenUtil.createToken(identity, apiKeySecret);

        // 封装返回前端(除了用户名、角色、时间戳保留，其余消去)
        identity.setToken(token);
        // identity.setId(id);
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
     * @return
     */
    public List<User> queryUserList(Integer pageNow, Integer pageSize, String role, String phone, String name, String
            type) {

        Example example = new Example(User.class);
        Example.Criteria criteria = example.createCriteria();

        if (!Validator.checkEmpty(name)) {
            criteria.andLike(Constant.NAME, "%" + name + "%");
        }

        if (!Validator.checkEmpty(phone)) {
            criteria.andLike(Constant.PHONE, "%" + phone + "%");
        }

        if (!Validator.checkEmpty(role)) {
            criteria.andLike(Constant.ROLE, "%" + role + "%");
        } else {
            if (type.equals(Constant.MEMBER)) {
                criteria.andLike(Constant.ROLE, "%会员%");
            } else { // type.equals("Constant.EMPLOYEE")
                criteria.andNotLike(Constant.ROLE, "%会员%");
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
            // 档案部员工或者主管，所有的会员
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
    public Set<Integer> queryMemberIdSetUnderEmployee(Identity identity) {

        List<User> memberList = this.queryMemberListUnderEmployee(identity);
        Set<Integer> memberSet = new HashSet<>();
        memberList.forEach(member -> memberSet.add(member.getId()));
        return memberSet;
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
     * 根据姓名模糊匹配，将匹配的结果的id组成set返回
     *
     * @param userName
     * @return
     */
    public Set<Integer> getMemberIdSetByUserNameLike(String userName) {
        return getIdSetByUserNameLike(userName, "会员");
    }


    public Set<Integer> getEmployeeIdSetByUserNameLike(String userName) {
        return this.getIdSetByUserNameLike(userName, "职员");
    }

    public Set<Integer> getIdSetByUserNameLike(String userName, String role) {

        Example userExample = new Example(User.class);
        Example.Criteria userCriteria = userExample.createCriteria();
        userCriteria.andLike("name", "%" + userName + "%");

        if (role.equals("会员")) {
            userCriteria.andLike("role", "%会员%");
        } else {
            userCriteria.andNotLike("role", "%会员%");
        }

        List<User> userList = this.getMapper().selectByExample(userExample);

        Set<Integer> userIdSet = new HashSet<>();
        userList.forEach(user -> userIdSet.add(user.getId()));

        return userIdSet;
    }
}
