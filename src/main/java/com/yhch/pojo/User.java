package com.yhch.pojo;

import java.util.Date;
import javax.persistence.*;

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "member_num")
    private String memberNum;

    private String username;

    private String password;

    private String avatar;

    private String name;

    private String role;

    @Column(name = "staff_id")
    private Integer staffId;

    @Column(name = "staff_mgr_id")
    private Integer staffMgrId;

    private Date valid;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return member_num
     */
    public String getMemberNum() {
        return memberNum;
    }

    /**
     * @param memberNum
     */
    public void setMemberNum(String memberNum) {
        this.memberNum = memberNum;
    }

    /**
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return avatar
     */
    public String getAvatar() {
        return avatar;
    }

    /**
     * @param avatar
     */
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    /**
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return role
     */
    public String getRole() {
        return role;
    }

    /**
     * @param role
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * @return staff_id
     */
    public Integer getStaffId() {
        return staffId;
    }

    /**
     * @param staffId
     */
    public void setStaffId(Integer staffId) {
        this.staffId = staffId;
    }

    /**
     * @return staff_mgr_id
     */
    public Integer getStaffMgrId() {
        return staffMgrId;
    }

    /**
     * @param staffMgrId
     */
    public void setStaffMgrId(Integer staffMgrId) {
        this.staffMgrId = staffMgrId;
    }

    /**
     * @return valid
     */
    public Date getValid() {
        return valid;
    }

    /**
     * @param valid
     */
    public void setValid(Date valid) {
        this.valid = valid;
    }
}