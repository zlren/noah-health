package com.yhch.service;

import com.yhch.pojo.ResultInputDetail;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by zlren on 2017/6/29.
 */
@Service
public class ResultInputDetailService extends BaseService<ResultInputDetail> {

    public void saveDetails(Integer inputId, List<Map<Integer, Object>> dataList) {
        dataList.forEach(data -> {
            Integer thirdId = (Integer) data.get("thirdId");
            String value = (String) data.get("value");
            ResultInputDetail resultInputDetail = new ResultInputDetail();
            resultInputDetail.setResultInputId(inputId);
            resultInputDetail.setThirdId(thirdId);
            resultInputDetail.setValue(value);
            this.save(resultInputDetail);
        });
    }
}
