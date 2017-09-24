package com.noahhealth.service;

import com.github.pagehelper.PageHelper;
import com.noahhealth.bean.Constant;
import com.noahhealth.bean.Identity;
import com.noahhealth.bean.input.ResultInputExtend;
import com.noahhealth.pojo.CategoryThird;
import com.noahhealth.pojo.ResultInput;
import com.noahhealth.pojo.ResultInputDetail;
import com.noahhealth.pojo.User;
import com.noahhealth.util.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
 * Created by zlren on 2017/6/12.
 */
@Service
@Slf4j
public class ResultInputService extends BaseService<ResultInput> {

    @Autowired
    private ResultInputDetailService resultInputDetailService;

    @Autowired
    private CategoryThirdService categoryThirdService;

    @Autowired
    private CategorySecondService categorySecondService;

    @Autowired
    private CategoryFirstService categoryFirstService;

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
     * 判断一条result记录是否异常
     * 从resultDetail表中去查找此inputeId的所有检查项目的normal
     *
     * @param inputerId
     * @return true表示异常
     */
    public boolean isError(Integer inputerId) {

        ResultInputDetail record = new ResultInputDetail();
        record.setResultInputId(inputerId);

        List<ResultInputDetail> resultInputDetailList = this.resultInputDetailService.queryListByWhere(record);

        log.info("检查{}", inputerId);

        final boolean[] result = {false};

        resultInputDetailList.forEach(resultInputDetail -> {
            if (resultInputDetail.getNormal() != null && resultInputDetail.getNormal()) {
                result[0] = true;
                log.info("inputerId: {} 有异常: {}", inputerId, resultInputDetail.getId());
            }
        });

        return result[0];
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
        resultInputList.forEach(resultInput -> resultInputExtendList.add(extendFromResultInput(resultInput)));
        return resultInputExtendList;
    }


    /**
     * 拓展单条记录
     *
     * @param resultInput
     * @return
     */
    public ResultInputExtend extendFromResultInput(ResultInput resultInput) {

        String userNameExtend = this.userService.queryById(resultInput.getUserId()).getName();
        String checkerNameExtend = null;
        if (resultInput.getCheckerId() != null) {
            checkerNameExtend = this.userService.queryById(resultInput.getCheckerId()).getName();
        }
        String inputerNameExtend = this.userService.queryById(resultInput.getInputerId()).getName();
        String secondNameExtend = this.categorySecondService.queryById(resultInput.getSecondId()).getName();
        String memberNum = this.userService.queryById(resultInput.getUserId()).getMemberNum();
        String type = this.categoryFirstService.queryById(categorySecondService.queryById(resultInput.getSecondId())
                .getFirstId()).getType();

        return new ResultInputExtend(resultInput, userNameExtend, secondNameExtend, inputerNameExtend,
                checkerNameExtend, memberNum, type);
    }


    /**
     * 查询化验、医技数据的用户列表（不是detail）
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

        Example example = new Example(User.class);
        Example.Criteria userCriteria = example.createCriteria();

        example.setOrderByClause("field(role,'三级会员','二级会员','一级会员','系统管理员','档案部主管','顾问部主管','档案部员工','顾问部员工', '财务部员工'), " +
                "member_num desc");

        if (!Validator.checkEmpty(userName)) {
            userCriteria.andLike(Constant.NAME, "%" + userName + "%");
        }

        if (!Validator.checkEmpty(memberNum)) {
            userCriteria.andLike("memberNum", "%" + memberNum + "%");
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
     * @param type
     * @param status
     * @param secondId
     * @param beginTime
     * @param endTime
     * @return
     */
    public List<ResultInput> queryResultAndDetailListByUserId(Identity identity, Integer userId, String type, String
            status, Integer secondId, Date beginTime, Date endTime) {

        String identityRole = identity.getRole();
        String identityId = identity.getId();

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


        // 档案部员工只能看到自己进行的任务
        if (this.userService.checkArchiver(identityRole)) {
            criteria.andEqualTo("inputerId", identityId);
        } else if (this.userService.checkArchiverManager(identityRole)) {
            criteria.andIn("inputerId", this.userService.queryArchiverIdSetByArchiveMgrId(Integer.valueOf
                    (identityId)));
        }


        return this.getMapper().selectByExample(example);
    }


    /**
     * 档案部查询用户的input信息
     *
     * @param pageNow
     * @param pageSize
     * @param userName
     * @param memberNum
     * @param beginTime
     * @param endTime
     * @param status
     * @param identity
     * @param inputerName
     * @param checkerName
     */
    public List<ResultInput> queryInputListByArc(Integer pageNow, Integer pageSize, String userName, String
            memberNum, Date beginTime, Date endTime, String status, Identity identity, String inputerName, String
                                                         checkerName) {

        String identityRole = identity.getRole();
        String identityId = identity.getId();

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

        // 如果是一个档案部员工，那就查所有和自己有关的记录
        if (this.userService.checkArchiver(identityRole)) {
            criteria.andEqualTo("inputerId", Integer.valueOf(identityId));
        } else if (this.userService.checkArchiverManager(identityRole)) { // 档案部主管，可以筛选录入者，因为自己手下有很多员工
            // ..
            Set<Integer> set = this.userService.queryArchiverIdSetByArchiveMgrId(Integer.valueOf(identityId));
            if (!Validator.checkEmpty(inputerName)) {
                Set<Integer> idSetByUserNameLike = this.userService.getEmployeeIdSetByUserNameLike(inputerName);
                set.retainAll(idSetByUserNameLike);
            }

            criteria.andIn("inputerId", set);
        } else if (this.userService.checkAdmin(identityRole)) {

            if (!Validator.checkEmpty(inputerName)) { // fixme
                criteria.andIn("inputerId", this.userService.getEmployeeIdSetByUserNameLike(inputerName));
            }

            if (!Validator.checkEmpty(checkerName)) {
                criteria.andIn("checkerId", this.userService.getEmployeeIdSetByUserNameLike(checkerName));
            }
        }

        Set<Integer> set = this.userService.getMemberIdSetByNameAndMemberNumLike(userName, memberNum);
        criteria.andIn("userId", set);

        PageHelper.startPage(pageNow, pageSize);
        return this.getMapper().selectByExample(example);
    }
}
