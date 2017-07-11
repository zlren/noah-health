package com.yhch.service;

import com.github.pagehelper.PageHelper;
import com.yhch.bean.Constant;
import com.yhch.bean.input.ResultInputExtend;
import com.yhch.pojo.CategoryThird;
import com.yhch.pojo.ResultInput;
import com.yhch.pojo.ResultInputDetail;
import com.yhch.util.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

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
        // this.resultInputDetailService.deleteByWhere(record);

        this.deleteById(inputId);

        return true;
    }

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

}
