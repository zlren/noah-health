package com.yhch.service;

import com.github.pagehelper.PageHelper;
import com.yhch.bean.Constant;
import com.yhch.bean.Identity;
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
     * @param identity
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
    public List<ResultOrigin> queryOriginList(Identity identity, Set<Integer> userIdSet, Set<String> statusSet, String status, String userName,
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

        // 能按上传者筛选的只有系统管理员和档案部主管
        // 档案部主管查询的时候，查出那些是自己的档案部员工上传的记录
        Set<Integer> valueUploaderIdSet = new HashSet<>();
        if (identity.getRole().equals(Constant.ARCHIVE_MANAGER)) {
            valueUploaderIdSet.addAll(this.userService.queryArchiverIdSetByArchiveMgrId(Integer.valueOf(identity.getId())));
        }

        if (!Validator.checkEmpty(uploaderName)) {
            criteria.andIn("uploaderId", this.userService.getEmployeeIdSetByUserNameLike(uploaderName));
        }

        if (!Validator.checkEmpty(checkerName)) {
            criteria.andIn("checkerId", this.userService.getEmployeeIdSetByUserNameLike(checkerName));
        }

        if (time != null) {
            // criteria.andEqualTo("time", time);
            criteria.andBetween("time", time, time);
        }

        PageHelper.startPage(pageNow, pageSize);
        return this.getMapper().selectByExample(example);
    }
}
