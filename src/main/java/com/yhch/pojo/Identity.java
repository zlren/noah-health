package com.yhch.pojo;

/**
 * Token携带信息
 * Created by ken on 2017/6/9.
 */
public class Identity {

	private String token;
	private String id; //对应user的id
	private String issuer;
	private String username;
	private String role; //角色
	private Long duration; //有效时长（毫秒）


	public String getToken() { return token; }

	public void setToken(String token) { this.token = token; }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIssuer() {
		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Long getDuration() {
		return duration;
	}

	public void setDuration(Long duration) {
		this.duration = duration;
	}
}
