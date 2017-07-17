package com.yhch.service;

import com.yhch.bean.input.ResultInputDetailExtend;
import com.yhch.pojo.CategoryThird;
import com.yhch.pojo.ResultInputDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zlren on 2017/6/29.
 */
@Service
public class ResultInputDetailService extends BaseService<ResultInputDetail> {

    @Autowired
    private CategoryThirdService categoryThirdService;

    @Autowired
    private ResultInputService resultInputService;

    /**
     * 将list中的数据插入数据库
     *
     * @param dataToSaveList
     */
    public void save(List<ResultInputDetail> dataToSaveList) {
        dataToSaveList.forEach(detail -> this.getMapper().updateByPrimaryKeySelective(detail));
    }

    /**
     * resultInputDetailList拓展为resultInputDetailExtendList
     *
     * @param resultInputDetailList
     * @return
     */
    public List<ResultInputDetailExtend> extendFromResultInputDetailList(List<ResultInputDetail>
                                                                                 resultInputDetailList) {
        List<ResultInputDetailExtend> resultInputDetailExtendList = new ArrayList<>();

        resultInputDetailList.forEach(resultInputDetail -> {

            CategoryThird categoryThird = this.categoryThirdService.queryById(resultInputDetail.getThirdId());
            String thirdName = categoryThird.getName();
            String referenceValue = categoryThird.getReferenceValue();
            // String systemCategory = categoryThird.getSystemCategory();
            // String hospital = categoryThird.getHospital();
            String hospital = this.resultInputService.queryById(resultInputDetail.getResultInputId()).getHospital();

            resultInputDetailExtendList.add(new ResultInputDetailExtend(resultInputDetail, thirdName, referenceValue,
                    hospital));
        });

        return resultInputDetailExtendList;
    }
}
