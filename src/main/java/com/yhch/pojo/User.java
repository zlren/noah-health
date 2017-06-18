package com.yhch.pojo;

import javax.persistence.*;

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String username;

    private String password;

    private String name;

    private String role;

    private String phone;

    @Column(name = "adviser_id")
    private String adviserId;

    @Column(name = "advise_mgr_id")
    private String adviseMgrId;

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
     * @return phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * @param phone
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * @return adviser_id
     */
    public String getAdviserId() {
        return adviserId;
    }

    /**
     * @param adviserId
     */
    public void setAdviserId(String adviserId) {
        this.adviserId = adviserId;
    }

    /**
     * @return advise_mgr_id
     */
    public String getAdviseMgrId() {
        return adviseMgrId;
    }

    /**
     * @param adviseMgrId
     */
    public void setAdviseMgrId(String adviseMgrId) {
        this.adviseMgrId = adviseMgrId;
    }
}