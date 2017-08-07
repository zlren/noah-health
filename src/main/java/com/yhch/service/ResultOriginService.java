package com.yhch.service;

import com.github.pagehelper.PageHelper;
import com.yhch.bean.Constant;
import com.yhch.bean.Identity;
import com.yhch.bean.origin.ResultOriginExtend;
import com.yhch.pojo.ResultOrigin;
import com.yhch.util.Validator;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
 * ResultOriginService
 * Created by zlren on 2017/6/12.
 */
@Service
public class ResultOriginService extends BaseService<ResultOrigin> {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ResultOriginService.class);

    @Autowired
    private UserService userService;

    @Autowired
    private OriginCategorySecondService originCategorySecondService;

    /**
     * 条件查询
     *
     * @param identity
     * @param pageNow
     * @param pageSize
     * @param status
     * @param userName
     * @param uploaderName
     * @param checkerName
     * @param beginTime
     * @param endTime
     * @return
     */
    public List<ResultOrigin> queryResultOriginList(Identity identity, Integer pageNow, Integer pageSize, String
            status, String userName, String uploaderName, String checkerName, String memberNum, Date beginTime, Date
                                                            endTime) {

        String identityId = identity.getId();
        String identityRole = identity.getRole();

        Example example = new Example(ResultOrigin.class);
        Example.Criteria originCriteria = example.createCriteria();

        example.setOrderByClause("time desc");

        {   // 时间和状态的筛选是统一的

            // 时间
            if (beginTime != null && endTime != null) {
                originCriteria.andBetween("time", beginTime, endTime);
            }

            // 状态
            Set<String> statusSet = this.userService.getStatusSetUnderRole(identity);
            if (!Validator.checkEmpty(status)) {
                Set<String> t = new HashSet<>();
                t.add(status);
                statusSet.retainAll(t);
            }
            originCriteria.andIn(Constant.STATUS, statusSet);
        }


        if (this.userService.checkAdmin(identityRole)) { // 系统管理员

            if (!Validator.checkEmpty(status)) {
                originCriteria.andEqualTo("status", status);
            }

            // if (!Validator.checkEmpty(userName)) {
            originCriteria.andIn("userId", this.userService.getMemberIdSetByNameAndMemberNumLike(userName, memberNum));
            // }

            // 上传者，档案部员工或者管理员
            if (!Validator.checkEmpty(uploaderName)) {
                originCriteria.andIn("uploaderId", this.userService.getIdSetByUserNameLikeAndRole(uploaderName,
                        "档案部员工"));
            }

            // 审核者可以是管理员，也可以是档案部主管
            if (!Validator.checkEmpty(checkerName)) {

                Set<Integer> adminSet = this.userService.getIdSetByUserNameLikeAndRole(checkerName, "管理员");
                Set<Integer> mgrSet = this.userService.getIdSetByUserNameLikeAndRole(checkerName, "档案部主管");
                adminSet.retainAll(mgrSet);

                originCriteria.andIn("checkerId", adminSet);
            }

        } else if (this.userService.checkArchiverManager(identityRole)) { // 档案部主管

            // 档案部主管对应的档案部员工
            // 根据上传者筛选后，就不用筛选审核者了，反正都是自己的员工做的
            Set<Integer> archiverIdSet = this.userService.queryArchiverIdSetByArchiveMgrId(Integer.valueOf(identityId));
            if (!Validator.checkEmpty(uploaderName)) {
                archiverIdSet.retainAll(this.userService.getIdSetByUserNameLikeAndRole(uploaderName, Constant
                        .ARCHIVER));
            }
            originCriteria.andIn("uploaderId", archiverIdSet);

            Set<Integer> memberSet = this.userService.queryMemberIdSetUnderRole(identity);
            memberSet.retainAll(this.userService.getMemberIdSetByNameAndMemberNumLike(userName, memberNum));
            originCriteria.andIn("userId", memberSet);

        } else if (this.userService.checkArchiver(identityRole)) { // 档案部员工

            // 只能看自己
            originCriteria.andEqualTo("uploaderId", identityId);

        } else if (this.userService.checkAdviseManager(identityRole)) { // 顾问部主管

            // 重在对userId的筛选，挑出是自己的顾问员工对应的会员
            Set<Integer> memberSet = this.userService.queryMemberIdSetUnderRole(identity);

            logger.info("哈哈哈");
            memberSet.forEach(member -> logger.info("{}", member));

            memberSet.retainAll(this.userService.getMemberIdSetByNameAndMemberNumLike(userName, memberNum));
            originCriteria.andIn("userId", memberSet);

        } else if (this.userService.checkAdviser(identityRole)) { // 顾问部员工

            // 重在对userId的筛选，挑出是自己的顾问员工对应的会员
            Set<Integer> memberSet = this.userService.queryMemberIdSetUnderRole(identity);
            memberSet.retainAll(this.userService.getMemberIdSetByNameAndMemberNumLike(userName, memberNum));
            originCriteria.andIn("userId", memberSet);

        } else if (this.userService.checkMember(identityRole)) {

            // 重在对userId的筛选，挑出是自己的顾问员工对应的会员
            Set<Integer> memberSet = this.userService.queryMemberIdSetUnderRole(identity);
            originCriteria.andIn("userId", memberSet);
        }


        PageHelper.startPage(pageNow, pageSize);
        return this.getMapper().selectByExample(example);
    }


    /**
     * 根据userId查询这个会员的所有的原始资料记录
     *
     * @param status
     * @param beginTime
     * @param endTime
     * @param identity
     * @return
     */
    public List<ResultOrigin> queryResultOriginListByUserId(Integer userId, String status, Date beginTime, Date
            endTime, Identity identity) {

        String identityId = identity.getId();
        String identityRole = identity.getRole();

        Example example = new Example(ResultOrigin.class);
        Example.Criteria originCriteria = example.createCriteria();

        example.setOrderByClause("time desc");

        {   // 时间和状态的筛选是统一的

            // 时间
            if (beginTime != null && endTime != null) {
                originCriteria.andBetween("time", beginTime, endTime);
            }

            // 状态
            Set<String> statusSet = this.userService.getStatusSetUnderRole(identity);
            if (!Validator.checkEmpty(status)) {
                Set<String> t = new HashSet<>();
                t.add(status);
                statusSet.retainAll(t);
            }
            originCriteria.andIn(Constant.STATUS, statusSet);
        }

        originCriteria.andEqualTo("userId", userId);

        return this.getMapper().selectByExample(example);
    }


    /**
     * 拓展
     *
     * @param resultOriginList
     * @return
     */
    public List<ResultOriginExtend> extendFromResultOriginList(List<ResultOrigin> resultOriginList) {

        List<ResultOriginExtend> resultOriginExtendList = new ArrayList<>();

        resultOriginList.forEach(resultOrigin -> {

            String memberNumExtend = this.userService.queryById(resultOrigin.getUserId()).getMemberNum();
            String userNameExtend = this.userService.queryById(resultOrigin.getUserId()).getName();
            String checkerNameExtend = null;
            if (resultOrigin.getCheckerId() != null) {
                checkerNameExtend = this.userService.queryById(resultOrigin.getCheckerId()).getName();
            }
            String uploaderNameExtend = this.userService.queryById(resultOrigin.getUploaderId()).getName();

            String originCategorySecondName = "";
            if (resultOrigin.getSecondId() != null) {
                originCategorySecondName = this.originCategorySecondService.queryById(resultOrigin.getSecondId())
                        .getName();
            }

            ResultOriginExtend resultOriginExtend = new ResultOriginExtend(resultOrigin, memberNumExtend,
                    userNameExtend, checkerNameExtend, uploaderNameExtend, originCategorySecondName);

            resultOriginExtendList.add(resultOriginExtend);
        });

        return resultOriginExtendList;
    }
}
