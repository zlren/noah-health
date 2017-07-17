package com.yhch.pojo;

import javax.persistence.*;
import java.util.Date;

@Table(name = "result_origin")
public class ResultOrigin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "input_time")
    private Date inputTime;

    private Date time;

    @Column(name = "uploader_id")
    private Integer uploaderId;

    @Column(name = "checker_id")
    private Integer checkerId;

    private String status;

    private String note;

    private String reason;

    private String path;

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
     * @return input_time
     */
    public Date getInputTime() {
        return inputTime;
    }

    /**
     * @param inputTime
     */
    public void setInputTime(Date inputTime) {
        this.inputTime = inputTime;
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
     * @return uploader_id
     */
    public Integer getUploaderId() {
        return uploaderId;
    }

    /**
     * @param uploaderId
     */
    public void setUploaderId(Integer uploaderId) {
        this.uploaderId = uploaderId;
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

    /**
     * @return reason
     */
    public String getReason() {
        return reason;
    }

    /**
     * @param reason
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * @return path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path
     */
    public void setPath(String path) {
        this.path = path;
    }
}