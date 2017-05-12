package com.yhch.service;

import com.yhch.pojo.User;
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

}
