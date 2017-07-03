package com.yhch.bean.input;

import com.yhch.pojo.ResultInputDetail;
import org.springframework.beans.BeanUtils;

/**
 * Created by zlren on 2017/6/29.
 */
public class ResultInputDetailExtend extends ResultInputDetail {

    public String thirdName;
    public String referenceValue;
    public String systemCategory;
    public String hospital;

    public ResultInputDetailExtend(ResultInputDetail resultInputDetail, String thirdName, String referenceValue,
                                   String systemCategory, String hospital) {
        BeanUtils.copyProperties(resultInputDetail, this);
        this.thirdName = thirdName;
        this.referenceValue = referenceValue;
        this.systemCategory = systemCategory;
        this.hospital = hospital;
    }
}
