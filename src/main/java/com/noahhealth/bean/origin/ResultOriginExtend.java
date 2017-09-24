package com.noahhealth.bean.origin;

import com.noahhealth.pojo.ResultOrigin;
import org.springframework.beans.BeanUtils;

/**
 * 原始数据列表中的一条记录
 * Created by zlren on 17/6/25.
 */
public class ResultOriginExtend extends ResultOrigin {

    public String memberNum;
    public String userName;
    public String checkerName;
    public String uploaderName;
    public String secondName;

    /**
     * 构造函数
     *
     * @param resultOrigin
     * @param memberNum
     * @param userName
     * @param checkerName
     * @param uploaderName
     */
    public ResultOriginExtend(ResultOrigin resultOrigin, String memberNum, String userName, String checkerName,
                              String uploaderName, String secondName) {
        BeanUtils.copyProperties(resultOrigin, this);
        this.memberNum = memberNum;
        this.userName = userName;
        this.checkerName = checkerName;
        this.uploaderName = uploaderName;
        this.secondName = secondName;
    }
}
