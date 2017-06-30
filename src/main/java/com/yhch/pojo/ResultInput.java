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

    @Column(name = "second_id")
    private Integer secondId;

    private Date time;

    @Column(name = "inputer_id")
    private Integer inputerId;

    @Column(name = "checker_id")
    private Integer checkerId;

    private String status;

    private String note;

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
     * @return second_id
     */
    public Integer getSecondId() {
        return secondId;
    }

    /**
     * @param secondId
     */
    public void setSecondId(Integer secondId) {
        this.secondId = secondId;
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

    /**
     * @return inputer_id
     */
    public Integer getInputerId() {
        return inputerId;
    }

    /**
     * @param inputerId
     */
    public void setInputerId(Integer inputerId) {
        this.inputerId = inputerId;
    }

    /**
     * @return checker_id
     */
    public Integer getCheckerId() {
        return checkerId;
    }

    /**
     * @param checkerId
     */
    public void setCheckerId(Integer checkerId) {
        this.checkerId = checkerId;
    }

    /**
     * @return status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return note
     */
    public String getNote() {
        return note;
    }

    /**
     * @param note
     */
    public void setNote(String note) {
        this.note = note;
    }
}