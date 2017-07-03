package com.yhch.service;

import com.yhch.pojo.ResultInputDetail;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by zlren on 2017/6/29.
 */
@Service
public class ResultInputDetailService extends BaseService<ResultInputDetail> {

    /**
     * 将list中的数据插入数据库
     *
     * @param dataToSaveList
     */
    public void save(List<ResultInputDetail> dataToSaveList) {
        dataToSaveList.forEach(detail -> this.getMapper().updateByPrimaryKeySelective(detail));
    }
}
