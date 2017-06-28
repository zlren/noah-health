package com.yhch.service;

import com.github.pagehelper.PageHelper;
import com.yhch.bean.Constant;
import com.yhch.pojo.ResultOrigin;
import com.yhch.util.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/**
 * Created by zlren on 2017/6/12.
 */
@Service
public class ResultOriginService extends BaseService<ResultOrigin> {

    private static final Logger logger = LoggerFactory.getLogger(ResultOriginService.class);

    public List<ResultOrigin> queryOriginList(String status, String userName,
                                              String uploaderName, String checkerName, Date time, Integer pageNow,
                                              Integer pageSize) {

        Example example = new Example(ResultOrigin.class);
        Example.Criteria criteria = example.createCriteria();

        if (!Validator.checkEmpty(status)) {
            criteria.andEqualTo(Constant.STATUS, status);
        }

        if (!Validator.checkEmpty(userName)) {
            criteria.andLike("userName", "%" + userName + "%");
        }

        if (!Validator.checkEmpty(uploaderName)) {
            criteria.andLike("uploaderName", "%" + uploaderName + "%");
        }

        if (!Validator.checkEmpty(checkerName)) {
            criteria.andLike("checkerName", "%" + checkerName + "%");
        }

        if (time != null) {
            criteria.andEqualTo("time", time);
        }

        PageHelper.startPage(pageNow, pageSize);
        return this.getMapper().selectByExample(example);
    }


}
