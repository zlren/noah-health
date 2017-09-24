package com.noahhealth.bean.input;

import com.noahhealth.pojo.ResultInput;
import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * Created by zlren on 2017/6/29.
 */
public class ResultInputExtend extends ResultInput {

    public String userName;
    public String secondName;
    public String inputerName;
    public String checkerName;
    public String memberNum;
    public String type; // 化验或医技

    public List<ResultInputDetailExtend> data;


    /**
     * 从ResultInput拓展为ResultInputExtend
     *
     * @param resultInput
     * @param userName
     * @param secondName
     * @param inputerName
     * @param checkerName
     * @param memberNum
     * @param type
     */
    public ResultInputExtend(ResultInput resultInput, String userName, String secondName, String
            inputerName, String checkerName, String memberNum, String type) {

        BeanUtils.copyProperties(resultInput, this);
        this.userName = userName;
        this.secondName = secondName;
        this.inputerName = inputerName;
        this.checkerName = checkerName;
        this.memberNum = memberNum;
        this.type = type;
    }
}
