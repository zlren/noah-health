package com.yhch.pojo;

import java.util.Date;
import javax.persistence.*;

@Table(name = "result_origin")
public class ResultOrigin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "user_name")
    private String userName;

    private Date time;

    @Column(name = "uploader_id")
    private Integer uploaderId;

    @Column(name = "uploader_name")
    private String uploaderName;

    @Column(name = "checker_id")
    private Integer checkerId;

    @Column(name = "checker_name")
    private String checkerName;

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
     * @return user_name
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName
     */
    public void setUserName(String userName) {
        this.userName = userName;
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
     * @return uploader_name
     */
    public String getUploaderName() {
        return uploaderName;
    }

    /**
     * @param uploaderName
     */
    public void setUploaderName(String uploaderName) {
        this.uploaderName = uploaderName;
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
     * @return checker_name
     */
    public String getCheckerName() {
        return checkerName;
    }

    /**
     * @param checkerName
     */
    public void setCheckerName(String checkerName) {
        this.checkerName = checkerName;
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