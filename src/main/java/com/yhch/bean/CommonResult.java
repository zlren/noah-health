package com.yhch.bean;

/**
 * Created by zlren on 2017/6/6.
 */
public class CommonResult {
    private String code;
    private String reason;


    public static CommonResult success() {
        return new CommonResult(Constant.SUCCESS, "");
    }

    public static CommonResult failure(String reason) {
        return new CommonResult(Constant.FAILURE, reason);
    }

    public CommonResult(String code, String reason) {
        this.code = code;
        this.reason = reason;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
