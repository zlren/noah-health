package com.yhch.service;

import com.yhch.bean.CommonResult;
import com.yhch.bean.Identity;
import com.yhch.pojo.User;
import com.yhch.util.MD5Util;
import com.yhch.util.TokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;

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

        if (super.queryOne(record) != null) {
            return false;
        }
        return true;
    }


    public User getUserByUsername(String username) {

        User record = new User();
        record.setUsername(username);

        return super.queryOne(record);
    }


    /**
     * 添加一个用户
     *
     * @param username
     * @param password
     * @param phoneNumber
     * @throws NoSuchAlgorithmException
     */
    public void register(String username, String password, String phoneNumber) throws NoSuchAlgorithmException {
        User user = new User();
        user.setUsername(username);
        user.setPassword(MD5Util.generate(password));
        user.setPhone(phoneNumber);
        super.save(user);
    }


    /**
     * 修改密码
     *
     * @param username
     * @param newPassword
     * @return
     */
    public int changePassword(String username, String newPassword) {
        // newPassword需要加密
        User record = this.getUserByUsername(username);
        record.setPassword(newPassword);

        return super.update(record);
    }


    /**
     * @param username
     * @param newUsername
     * @return
     */
    public int changeUsername(String username, String newUsername) {

        User user = getUserByUsername(newUsername);
        if (user != null) {
            // 说明用户名重复了
            return 0;
        }

        User curUser = this.getUserByUsername(username);
        curUser.setUsername(newUsername);
        super.update(curUser);

        return 1;
    }


    /**
     * 为通过登录验证的用户生成token
     *
     * @param username
     * @return
     */
    public CommonResult generateToken(String id, String issuer, String username, String role, Long duration, String
            apiKeySecret) {

        Identity identity = new Identity();
        identity.setId(id);
        identity.setIssuer(issuer);
        identity.setUsername(username);
        identity.setRole(role);
        identity.setDuration(duration);
        String token = TokenUtil.createToken(identity, apiKeySecret);

        // 封装返回前端(除了用户名、角色、时间戳保留，其余消去)
        identity.setToken(token);
        identity.setId(null);
        identity.setIssuer(null);
        return CommonResult.success("登录成功", identity);
    }
}
