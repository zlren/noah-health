package com.yhch.bean.input;

import com.yhch.pojo.ResultInputDetail;
import org.springframework.beans.BeanUtils;

/**
 * Created by zlren on 2017/6/29.
 */
public class ResultInputDetailExtend extends ResultInputDetail {

    public String thirdName;
    public String referenceValue;
    public String hospital;
    public String enShort;

    /**
     * 构造函数
     *
     * @param resultInputDetail
     * @param thirdName
     * @param referenceValue
     * @param hospital
     */
    public ResultInputDetailExtend(ResultInputDetail resultInputDetail, String thirdName, String referenceValue,
                                   String hospital, String enShort) {
        BeanUtils.copyProperties(resultInputDetail, this);
        this.thirdName = thirdName;
        this.referenceValue = referenceValue;
        this.hospital = hospital;
        this.enShort = enShort;
    }
}
