package com.yhch.service.shiro;

import com.yhch.bean.ROLES;
import com.yhch.pojo.Member;
import com.yhch.pojo.User;
import com.yhch.service.MemberService;
import com.yhch.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class CustomRealm extends AuthorizingRealm {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CustomRealm.class);

    // 注入service
    @Autowired
    private UserService userService;

    @Autowired
    private MemberService memberService;

    /**
     * @param token
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        // logger.info("身份认证");
        // 从token中取出身份信息
        String username = (String) token.getPrincipal();
        logger.info("username: " + username);

        User activeUser = userService.getUserByUsername(username);

        if (activeUser == null) {
            logger.info("无此用户");
            return null;
        }

        // activeUser的extend域填充了用户的姓名(member.name)
        Member activeMember = memberService.queryById(activeUser.getId());
        activeUser.setName(activeMember.getName());

        SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo(activeUser,
                activeUser.getPassword(), this.getName());

        return simpleAuthenticationInfo;
    }

    /**
     * 用于授权
     *
     * @param principals
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

        User activeUser = (User) principals.getPrimaryPrincipal();
        int auth = activeUser.getAuth();

        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();

        String role = null;
        String info = null;

        switch (auth) {
            case 0:
                simpleAuthorizationInfo.addRole(ROLES.ADMIN);
                role = ROLES.ADMIN;
                info = "超级管理员";
                break;
            case 1:
                simpleAuthorizationInfo.addRole(ROLES.FINANCER);
                role = ROLES.FINANCER;
                info = "财务";
                break;
            case 2:
                simpleAuthorizationInfo.addRole(ROLES.ARCHIVER);
                role = ROLES.ARCHIVER;
                info = "档案部员工";
                break;
            case 3:
                simpleAuthorizationInfo.addRole(ROLES.ARCHIVE_MANAGER);
                role = ROLES.ARCHIVE_MANAGER;
                info = "档案部主管";
                break;
            case 4:
                simpleAuthorizationInfo.addRole(ROLES.ADVISER);
                role = ROLES.ADVISER;
                info = "顾问";
                break;
            case 5:
                simpleAuthorizationInfo.addRole(ROLES.ADVISE_MANAGER);
                role = ROLES.ADVISE_MANAGER;
                info = "顾问部主管";
                break;
            case 6:
                simpleAuthorizationInfo.addRole(ROLES.USER_1);
                role = ROLES.USER_1;
                info = "一级用户";
                break;
            case 7:
                simpleAuthorizationInfo.addRole(ROLES.USER_2);
                role = ROLES.USER_2;
                info = "二级用户";
                break;
            case 8:
                simpleAuthorizationInfo.addRole(ROLES.USER_3);
                role = ROLES.USER_3;
                info = "三级用户";
                break;
        }

        logger.info("当前用户的角色为: {} -- {}", role, info);

        return simpleAuthorizationInfo;
    }

    /**
     * 清除缓存
     */
    public void clearCached() {
        PrincipalCollection principals = SecurityUtils.getSubject().getPrincipals();
        super.clearCache(principals);
    }
}
