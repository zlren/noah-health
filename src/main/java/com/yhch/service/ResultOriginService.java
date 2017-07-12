package com.yhch.service;

import com.github.pagehelper.PageHelper;
import com.yhch.bean.Constant;
import com.yhch.pojo.ResultOrigin;
import com.yhch.util.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * ResultOriginService
 * Created by zlren on 2017/6/12.
 */
@Service
public class ResultOriginService extends BaseService<ResultOrigin> {

    @Autowired
    private UserService userService;

    /**
     *
     * @param userIdSet
     * @param statusSet
     * @param status
     * @param userName
     * @param uploaderName
     * @param checkerName
     * @param time
     * @param pageNow
     * @param pageSize
     * @return
     */
    public List<ResultOrigin> queryOriginList(Set<Integer> userIdSet, Set<String> statusSet, String status, String userName,
                                              String uploaderName, String checkerName, Date time, Integer pageNow,
                                              Integer pageSize) {

        Example example = new Example(ResultOrigin.class);
        Example.Criteria criteria = example.createCriteria();

        // status
        Set<String> valueStatusSet = new HashSet<>();
        valueStatusSet.addAll(statusSet);
        if (!Validator.checkEmpty(status)) {
            Set<String> t = new HashSet<>();
            t.add(status);
            valueStatusSet.retainAll(t);
        }
        criteria.andIn(Constant.STATUS, valueStatusSet);

        // id和name共同考虑
        Set<Integer> valueIdSet = new HashSet<>();
        valueIdSet.addAll(userIdSet);
        if (!Validator.checkEmpty(userName)) {
            valueIdSet.retainAll(this.userService.getMemberIdSetByUserNameLike(userName));
        }
        criteria.andIn("userId", valueIdSet);

        if (!Validator.checkEmpty(uploaderName)) {
            criteria.andIn("uploaderId", this.userService.getEmployeeIdSetByUserNameLike(uploaderName));
        }

        if (!Validator.checkEmpty(checkerName)) {
            criteria.andIn("checkerId", this.userService.getEmployeeIdSetByUserNameLike(checkerName));
        }

        if (time != null) {
            criteria.andEqualTo("time", time);
        }

        PageHelper.startPage(pageNow, pageSize);
        return this.getMapper().selectByExample(example);
    }
}
