package com.yhch.bean;

/**
 * Created by zlren on 2017/6/6.
 */
public class CommonResult {
	private String code;
	private String reason;
	private Object content; //如果成功所返回的实际内容


	public static CommonResult success(String reason, Object content) { return new CommonResult(Constant.SUCCESS, reason, content);}

	public static CommonResult failure(String reason) {
		return new CommonResult(Constant.FAILURE, reason, null);
	}

	public CommonResult(String code, String reason, Object content) {
		this.code = code;
		this.reason = reason;
		this.content = content;
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

	public Object getContent() {
		return content;
	}

	public void setContent(Object content) {
		this.content = content;
	}
}
