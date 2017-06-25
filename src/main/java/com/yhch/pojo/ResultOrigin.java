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

    @Column(name = "user_name")
    private String userName;

    @Column(name = "second_id")
    private Integer secondId;

    @Column(name = "second_name")
    private String secondName;

    private String path;

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

    @Column(name = "inputer_id")
    private Integer inputerId;

    @Column(name = "inputer_name")
    private String inputerName;

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
     * @return second_name
     */
    public String getSecondName() {
        return secondName;
    }

    /**
     * @param secondName
     */
    public void setSecondName(String secondName) {
        this.secondName = secondName;
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
     * @return inputer_name
     */
    public String getInputerName() {
        return inputerName;
    }

    /**
     * @param inputerName
     */
    public void setInputerName(String inputerName) {
        this.inputerName = inputerName;
    }
}