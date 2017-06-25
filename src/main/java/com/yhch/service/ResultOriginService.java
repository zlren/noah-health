package com.yhch.service;

import com.github.pagehelper.PageHelper;
import com.yhch.bean.Constant;
import com.yhch.pojo.ResultOrigin;
import com.yhch.util.Validator;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/**
 * Created by zlren on 2017/6/12.
 */
@Service
public class ResultOriginService extends BaseService<ResultOrigin> {

    public List<ResultOrigin> queryOriginList(String status, String userName, String secondName, String uploaderName,
                                              String checkerName, String inputerName, Date time,
                                              Integer pageNow, Integer pageSize) {

        Example example = new Example(ResultOrigin.class);
        Example.Criteria criteria = example.createCriteria();

        if (!Validator.checkEmpty(status)) {
            criteria.andEqualTo(Constant.STATUS, status);
        }

        if (!Validator.checkEmpty(userName)) {
            criteria.andLike("user_name", userName);
        }

        if (!Validator.checkEmpty(secondName)) {
            criteria.andLike("second_name", secondName);
        }

        if (!Validator.checkEmpty(uploaderName)) {
            criteria.andLike("uploader_name", uploaderName);
        }

        if (!Validator.checkEmpty(checkerName)) {
            criteria.andLike("checker_name", checkerName);
        }

        if (!Validator.checkEmpty(inputerName)) {
            criteria.andLike("inputer_name", inputerName);
        }

        // if (time != null) {
        //     criteria.ande
        // }

        PageHelper.startPage(pageNow, pageSize);
        return this.getMapper().selectByExample(example);
    }




}
