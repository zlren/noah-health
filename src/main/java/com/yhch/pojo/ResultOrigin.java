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

    @Column(name = "second_id")
    private Integer secondId;

    private String path;

    private Date time;

    @Column(name = "uploader_id")
    private Integer uploaderId;

    @Column(name = "checker_id")
    private Integer checkerId;

    @Column(name = "is_pass")
    private String isPass;

    @Column(name = "is_input")
    private String isInput;

    @Column(name = "inputer_id")
    private Integer inputerId;

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
     * @return is_pass
     */
    public String getIsPass() {
        return isPass;
    }

    /**
     * @param isPass
     */
    public void setIsPass(String isPass) {
        this.isPass = isPass;
    }

    /**
     * @return is_input
     */
    public String getIsInput() {
        return isInput;
    }

    /**
     * @param isInput
     */
    public void setIsInput(String isInput) {
        this.isInput = isInput;
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
}