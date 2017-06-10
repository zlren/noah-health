package com.yhch.bean.sms;

import java.util.Date;

/**
 * sms_code + time
 * Created by zlren on 2017/6/10.
 */
public class SmsCode {

    private String smsCode;
    private Long date;

    public SmsCode(String smsCode) {
        this.smsCode = smsCode;
        this.date = new Date().getTime();
    }

    public SmsCode(String smsCode, Long date) {
        this.smsCode = smsCode;
        this.date = date;
    }

    public String getSmsCode() {
        return smsCode;
    }

    public void setSmsCode(String smsCode) {
        this.smsCode = smsCode;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }
}
