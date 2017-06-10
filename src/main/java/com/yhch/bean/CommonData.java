package com.yhch.bean;

import com.yhch.bean.sms.SmsCode;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 单例模式
 * Created by zlren on 2017/6/10.
 */
public class CommonData {

    private static final CommonData instance = new CommonData();

    private Map<String, SmsCode> smsCodeMap = new ConcurrentHashMap<>();

    private CommonData() {

    }

    public static CommonData getInstance() {
        return instance;
    }

    public void setMsgCode(String phoneNumber, String smsCode) {
        smsCodeMap.put(phoneNumber, new SmsCode(smsCode));
    }

    public boolean checkCode(String phoneNumber, String inputSmsCode) {

        // 系统生成的正确的验证码和创建时间
        SmsCode targetSmsCode = smsCodeMap.get(phoneNumber);

        if (targetSmsCode.getSmsCode().equals(inputSmsCode) && Math.abs(new Date().getTime() - targetSmsCode.getDate
                ()) < 60000) {
            return true;
        }
        return false;
    }

    public boolean sendCheck(String phone) {
        if (!smsCodeMap.containsKey(phone)) {
            return true;
        } else {
            Long lastTime = smsCodeMap.get(phone).getDate();
            long now = new Date().getTime();
            if (Math.abs(now - lastTime) < 60 * 1000) {
                return false;
            }
        }
        return true;
    }
}
