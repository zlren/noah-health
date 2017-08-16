package com.yhch.service;

import com.yhch.bean.Constant;
import com.yhch.bean.Identity;
import com.yhch.bean.health.ResultHealthExtend;
import com.yhch.pojo.ResultHealth;
import com.yhch.pojo.User;
import com.yhch.util.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ResultHealthService extends BaseService<ResultHealth> {

    @Autowired
    private ResultInputService resultInputService;

    @Autowired
    private UserService userService;

    @Autowired
    private HealthCategorySecondService healthCategorySecondService;

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
    public List<User> queryResultHealthUserList(Identity identity, String userName, String memberNum, Integer pageNow,
                                                Integer pageSize) {

        // 引用了input表的user展现，两者的逻辑是一致的
        return resultInputService.queryResultInputUserList(identity, userName, memberNum, pageNow, pageSize);
    }


    /**
     * 根据userId查询会员的健康摘要列表
     *
     * @param identity
     * @param userId
     * @param status
     * @param secondId
     * @param beginTime
     * @param endTime
     * @return
     */
    public List<ResultHealth> queryResultHealthListByUserId(Identity identity, Integer userId, String status, Integer
            secondId, Date beginTime, Date endTime) {

        String identityRole = identity.getRole();
        String identityId = identity.getId();

        Example example = new Example(ResultHealth.class);
        Example.Criteria criteria = example.createCriteria();

        example.setOrderByClause("time DESC"); // 倒叙

        {   // 时间和状态的筛选是统一的

            // 时间
            if (beginTime != null && endTime != null) {
                criteria.andBetween("time", beginTime, endTime);
            }


            if (!Validator.checkEmpty(status)) {
                criteria.andEqualTo(Constant.STATUS, status);
            }

            // 会员看content字段
            if (this.userService.checkMember(identityRole)) {
                criteria.andCondition("length(content)>", 0);
            }
        }

        criteria.andEqualTo("userId", userId);

        if (secondId != -1) {
            criteria.andEqualTo("secondId", secondId);
        }

        // 顾问部员工只能看到自己进行的任务
        if (this.userService.checkAdviser(identityRole)) {
            criteria.andEqualTo("inputerId", identityId);
        } else if (this.userService.checkAdviseManager(identityRole)) {
            criteria.andIn("inputerId", this.userService.queryAdviserIdSetByAdviseMgrId(Integer.valueOf
                    (identityId)));
        }

        return this.getMapper().selectByExample(example);
    }


    /**
     * 拓展
     *
     * @param resultHealthList
     * @return
     */
    public List<ResultHealthExtend> extendFromResultHealthList(List<ResultHealth> resultHealthList) {

        List<ResultHealthExtend> resultHealthExtendList = new ArrayList<>();

        resultHealthList.forEach(resultHealth -> {

            // resultHealth.setContentNew(null); // 防止正在提交审核的数据泄漏

            String userNameExtend = this.userService.queryById(resultHealth.getUserId()).getName();
            String checkerNameExtend = null;
            if (resultHealth.getCheckerId() != null) {
                checkerNameExtend = this.userService.queryById(resultHealth.getCheckerId()).getName();
            }
            String inputerNameExtend = this.userService.queryById(resultHealth.getInputerId()).getName();
            String secondNameExtend = this.healthCategorySecondService.queryById(resultHealth.getSecondId()).getName();

            String memberNum = this.userService.queryById(resultHealth.getUserId()).getMemberNum();
            resultHealthExtendList.add(new ResultHealthExtend(resultHealth, userNameExtend, secondNameExtend,
                    inputerNameExtend, checkerNameExtend, memberNum));
        });

        return resultHealthExtendList;
    }
}
