package com.yhch.service;

import com.yhch.bean.Identity;
import com.yhch.pojo.ResultHealth;
import com.yhch.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResultHealthService extends BaseService<ResultHealth> {

    @Autowired
    private ResultInputService resultInputService;

    /**
     * 调用的是resultInputService的UserList展现
     *
     * @param identity
     * @param userName
     * @param memberNum
     * @param pageNow
     * @param pageSize
     * @return
     */
    public List<User> queryResultInputUserList(Identity identity, String userName, String memberNum, Integer pageNow,
                                               Integer pageSize) {

        // 引用了input表的user展现，两者的逻辑是一致的
        return resultInputService.queryResultInputUserList(identity, userName, memberNum, pageNow, pageSize);
    }
}
