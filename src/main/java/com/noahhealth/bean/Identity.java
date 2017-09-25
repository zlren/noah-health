package com.noahhealth.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Token携带信息
 * Created by ken on 2017/6/9.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Identity {

    private String token;
    private String id; // 对应user的id
    private String issuer;
    private String username;
    private String role; // 角色
    private String name;
    private Long duration; // 有效时长，单位毫秒
    private String avatar;
}
