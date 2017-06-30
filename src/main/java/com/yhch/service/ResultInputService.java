package com.yhch.service;

import com.yhch.pojo.ResultInput;
import com.yhch.pojo.ResultInputDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by zlren on 2017/6/12.
 */
@Service
public class ResultInputService extends BaseService<ResultInput> {

    @Autowired
    private ResultInputDetailService resultInputDetailService;

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
}
