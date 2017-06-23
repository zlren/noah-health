package com.yhch.pojo;

import java.util.Date;
import javax.persistence.*;

@Table(name = "result_input")
public class ResultInput {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "origin_id")
    private Integer originId;

    @Column(name = "third_id")
    private Integer thirdId;

    private String value;

    private Date time;

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
     * @return user_id
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * @param userId
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * @return origin_id
     */
    public Integer getOriginId() {
        return originId;
    }

    /**
     * @param originId
     */
    public void setOriginId(Integer originId) {
        this.originId = originId;
    }

    /**
     * @return third_id
     */
    public Integer getThirdId() {
        return thirdId;
    }

    /**
     * @param thirdId
     */
    public void setThirdId(Integer thirdId) {
        this.thirdId = thirdId;
    }

    /**
     * @return value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return time
     */
    public Date getTime() {
        return time;
    }

    /**
     * @param time
     */
    public void setTime(Date time) {
        this.time = time;
    }
}