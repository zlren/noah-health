package com.yhch.bean.origin;

import com.yhch.pojo.ResultOrigin;

/**
 * 原始数据列表中的一条记录
 * Created by zlren on 17/6/25.
 */
public class OriginRecordInList extends ResultOrigin {

    private String userName;
    private String secondName;
    private String checkerName;
    private String uploaderName;
    private String inputerName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getCheckerName() {
        return checkerName;
    }

    public void setCheckerName(String checkerName) {
        this.checkerName = checkerName;
    }

    public String getUploaderName() {
        return uploaderName;
    }

    public void setUploaderName(String uploaderName) {
        this.uploaderName = uploaderName;
    }

    public String getInputerName() {
        return inputerName;
    }

    public void setInputerName(String inputerName) {
        this.inputerName = inputerName;
    }
}
