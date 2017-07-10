package com.yhch.bean.input;

import com.yhch.pojo.ResultInput;
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

    public List<ResultInputDetailExtend> data;


    /**
     * 从ResultInput拓展为ResultInputExtend
     *
     * @param resultInput
     */
    public ResultInputExtend(ResultInput resultInput, String userName, String secondName, String
            inputerName, String checkerName) {

        BeanUtils.copyProperties(resultInput, this);
        this.userName = userName;
        this.secondName = secondName;
        this.inputerName = inputerName;
        this.checkerName = checkerName;
    }
}
