package com.yhch.service;

import com.github.pagehelper.PageHelper;
import com.yhch.bean.Constant;
import com.yhch.bean.Identity;
import com.yhch.bean.input.ResultInputExtend;
import com.yhch.pojo.CategoryThird;
import com.yhch.pojo.ResultInput;
import com.yhch.pojo.ResultInputDetail;
import com.yhch.pojo.User;
import com.yhch.util.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
 * Created by zlren on 2017/6/12.
 */
@Service
public class ResultInputService extends BaseService<ResultInput> {

    private static final Logger logger = LoggerFactory.getLogger(ResultInputService.class);

    @Autowired
    private ResultInputDetailService resultInputDetailService;

    @Autowired
    private CategoryThirdService categoryThirdService;

    @Autowired
    private CategorySecondService categorySecondService;

    @Autowired
    private UserService userService;

    /**
     * 级联删除
     *
     * @param inputId
     * @return
     */
    public boolean deleteInput(Integer inputId) {

        ResultInput resultInput = this.queryById(inputId);

        if (resultInput == null) {
            return false;
        }

        ResultInputDetail record = new ResultInputDetail();
        record.setResultInputId(resultInput.getId());
        this.resultInputDetailService.deleteByWhere(record);

        this.deleteById(inputId);

        return true;
    }

    /**
     * @param userIdSet
     * @param status
     * @param userName
     * @param inputerName
     * @param checkerName
     * @param secondName
     * @param hospital
     * @param time
     * @param pageNow
     * @param pageSize
     * @return
     */
    public List<ResultInput> queryInputList(Set<Integer> userIdSet, String status, String userName, String
            inputerName, String checkerName, String secondName, String hospital, Date time, Integer pageNow, Integer
                                                    pageSize) {

        Example example = new Example(ResultInput.class);
        Example.Criteria criteria = example.createCriteria();

        if (!Validator.checkEmpty(status)) {
            criteria.andEqualTo(Constant.STATUS, status);
        }

        if (!Validator.checkEmpty(userName)) {
            // fixme userIdSet是旗下的人，和这里得到的userName对应的idSet可以做交集！！！
            criteria.andIn("userId", this.userService.getMemberIdSetByUserNameLike(userName));
        }

        if (!Validator.checkEmpty(inputerName)) {
            criteria.andIn("inputerId", this.userService.getEmployeeIdSetByUserNameLike(inputerName));
        }

        if (!Validator.checkEmpty(checkerName)) {
            criteria.andIn("checkerId", this.userService.getEmployeeIdSetByUserNameLike(checkerName));
        }

        if (!Validator.checkEmpty(secondName)) {
            criteria.andIn("secondId", this.categorySecondService.getIdSetBySecondNameLike(secondName));
        }

        if (!Validator.checkEmpty(hospital)) {
            criteria.andLike("hospital", "%" + hospital + "%");
        }

        if (time != null) {
            criteria.andEqualTo("time", time);
        }

        // // 只能查看旗下的人的资料
        // if (userIdSet != null && userIdSet.size() > 0) {
        //     criteria.andIn("userId", userIdSet);
        // }

        PageHelper.startPage(pageNow, pageSize);
        return this.getMapper().selectByExample(example);
    }

    /**
     * 级联插入
     *
     * @param resultInput
     */
    public void saveInputAndEmptyDetail(ResultInput resultInput) {

        this.save(resultInput);

        // 回填id
        Integer resultInputId = resultInput.getId();

        CategoryThird record = new CategoryThird();
        record.setSecondId(resultInput.getSecondId());
        List<CategoryThird> categoryThirdList = this.categoryThirdService.queryListByWhere(record);

        categoryThirdList.forEach(categoryThird -> {
            ResultInputDetail resultInputDetail = new ResultInputDetail();
            resultInputDetail.setResultInputId(resultInputId);
            resultInputDetail.setThirdId(categoryThird.getId());
            this.resultInputDetailService.save(resultInputDetail);
        });
    }

    /**
     * 将resultInputList转为resultInputExtendList
     *
     * @param resultInputList
     * @return
     */
    public List<ResultInputExtend> extendFromResultInputList(List<ResultInput> resultInputList) {

        List<ResultInputExtend> resultInputExtendList = new ArrayList<>();

        resultInputList.forEach(resultInput -> {

            String userNameExtend = this.userService.queryById(resultInput.getUserId()).getName();
            String checkerNameExtend = null;
            if (resultInput.getCheckerId() != null) {
                checkerNameExtend = this.userService.queryById(resultInput.getCheckerId()).getName();
            }
            String inputerNameExtend = this.userService.queryById(resultInput.getInputerId()).getName();
            String secondNameExtend = this.categorySecondService.queryById(resultInput.getSecondId()).getName();

            resultInputExtendList.add(new ResultInputExtend(resultInput, userNameExtend, secondNameExtend,
                    inputerNameExtend, checkerNameExtend));
        });

        return resultInputExtendList;
    }


    /**
     * 查询化验、医技数据的用户列表（不是detail）
     *
     * @param identity
     * @param userName
     * @param pageNow
     * @param pageSize
     * @return
     */
    public List<User> queryResultInputUserList(Identity identity, String userName, Integer pageNow, Integer pageSize) {

        Example example = new Example(User.class);
        Example.Criteria userCriteria = example.createCriteria();

        example.setOrderByClause("role DESC");

        if (!Validator.checkEmpty(userName)) {
            userCriteria.andLike(Constant.NAME, "%" + userName + "%");
        }

        // 化验医技数据，一级用户没有
        Set<String> twoOrThree = new HashSet<>();
        twoOrThree.add(Constant.USER_2);
        twoOrThree.add(Constant.USER_3);
        userCriteria.andIn("role", twoOrThree);

        Set<Integer> usersSet = this.userService.queryMemberIdSetUnderRole(identity);
        userCriteria.andIn("id", usersSet);

        PageHelper.startPage(pageNow, pageSize);
        return this.userService.getMapper().selectByExample(example);
    }

    /**
     * 根据userId查询详细的input和detail列表
     *
     * @param identity
     * @param userId
     * @param s
     * @param type
     * @param secondId
     * @param beginTime
     * @param endTime   @return
     */
    public List<ResultInput> queryResultAndDetailListByUserId(Identity identity, Integer userId, String type, String
            status, Integer secondId, Date beginTime, Date endTime) {

        Example example = new Example(ResultInput.class);
        Example.Criteria criteria = example.createCriteria();

        example.setOrderByClause("time DESC"); // 倒叙

        {   // 时间和状态的筛选是统一的

            // 时间
            if (beginTime != null && endTime != null) {
                criteria.andBetween("time", beginTime, endTime);
            }

            // 状态
            Set<String> statusSet = this.userService.getStatusSetUnderRole(identity);
            if (!Validator.checkEmpty(status)) {
                Set<String> t = new HashSet<>();
                t.add(status);
                statusSet.retainAll(t);
            }
            criteria.andIn(Constant.STATUS, statusSet);
        }

        criteria.andEqualTo("userId", userId);

        if (secondId != -1) {
            criteria.andEqualTo("secondId", secondId);
        } else {
            criteria.andIn("secondId", this.categorySecondService.getSecondIdSetByFirstType(type));
        }

        return this.getMapper().selectByExample(example);
    }
}
