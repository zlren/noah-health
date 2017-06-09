package com.yhch.service;

import com.yhch.bean.CommonResult;
import com.yhch.pojo.Identity;
import com.yhch.pojo.User;
import com.yhch.util.MD5Util;
import com.yhch.util.TokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserService extends BaseService<User> {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public User getUserByUsername(String username) {

        User record = new User();
        record.setUsername(username);

        return super.queryOne(record);
    }

    /**
     * 修改密码
     *
     * @param username
     * @param newPassword
     * @return
     */
    public int changePassword(String username, String newPassword) {

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
     * 验证用户名与密码
     * @param username
     * @param password
     * @return token
     */
    public CommonResult loginValidate(String username, String password) {

        //验证用户名与密码是否与数据库中匹配
        User user = new User();
        user.setUsername(username);
        User targetUser = getMapper().selectOne(user);
        if(targetUser == null) return CommonResult.failure("无此用户");

        //加密密码
        try {
            password = MD5Util.generate(password);
        } catch(Exception e){
            logger.info("MD5加密失败");
            return CommonResult.failure("加密失败");
        }

        //匹配验证
        if(!targetUser.getPassword().equals(password)) return CommonResult.failure("密码错误");

        return CommonResult.success("用户名与密码匹配成功", targetUser);
    }

    /**
     * 为通过登录验证的用户生成token
     * @param username
     * @return
     */
    public CommonResult generateToken(String id, String issuer, String username, String role, Long duration, String apiKeySecret) {

        Identity identity = new Identity();
        identity.setId(id);
        identity.setIssuer(issuer);
        identity.setUsername(username);
        identity.setRole(role);
        identity.setDuration(duration);
        String token = TokenUtil.createToken(identity, apiKeySecret);

        //封装返回前端(除了用户名、角色、时间戳保留，其余消去)
        identity.setToken(token);
        identity.setId(null);
        identity.setIssuer(null);
        return CommonResult.success("登录成功", identity);
    }
}
