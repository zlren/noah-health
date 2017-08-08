package com.yhch.bean.health;

import com.yhch.pojo.ResultHealth;
import org.springframework.beans.BeanUtils;

public class ResultHealthExtend extends ResultHealth {

    public String userName;
    public String secondName;
    public String inputerName;
    public String checkerName;
    public String memberNum;


    /**
     * @param resultInput
     * @param userName
     * @param secondName
     * @param inputerName
     * @param checkerName
     */
    public ResultHealthExtend(ResultHealth resultHealth, String userName, String secondName, String
            inputerName, String checkerName, String memberNum) {

        BeanUtils.copyProperties(resultHealth, this);
        this.userName = userName;
        this.secondName = secondName;
        this.inputerName = inputerName;
        this.checkerName = checkerName;
        this.memberNum = memberNum;
    }
}
