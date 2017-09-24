package com.noahhealth.pojo;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

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
     * 出生日期
     */
    private Date birth;

    /**
     * 性别
     */
    private String gender;

    /**
     * 身份证号
     */
    @Column(name = "id_card")
    private String idCard;

    /**
     * 身体状况
     */
    @Column(name = "physical_condition")
    private String physicalCondition;

    /**
     * 婚姻状况
     */
    @Column(name = "marital_status")
    private String maritalStatus;

    /**
     * 医疗费别
     */
    @Column(name = "medical_care")
    private String medicalCare;

    /**
     * 医保定点医院
     */
    private String hospital;

    /**
     * 商业健康保险
     */
    private String insurance;

    /**
     * 过敏_药物
     */
    @Column(name = "allergy_drug")
    private String allergyDrug;

    /**
     * 过敏_其他
     */
    @Column(name = "allergy_others")
    private String allergyOthers;

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

    /**
     * 获取出生日期
     *
     * @return birth - 出生日期
     */
    public Date getBirth() {
        return birth;
    }

    /**
     * 设置出生日期
     *
     * @param birth 出生日期
     */
    public void setBirth(Date birth) {
        this.birth = birth;
    }

    /**
     * 获取性别
     *
     * @return gender - 性别
     */
    public String getGender() {
        return gender;
    }

    /**
     * 设置性别
     *
     * @param gender 性别
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * 获取身份证号
     *
     * @return id_card - 身份证号
     */
    public String getIdCard() {
        return idCard;
    }

    /**
     * 设置身份证号
     *
     * @param idCard 身份证号
     */
    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    /**
     * 获取身体状况
     *
     * @return physical_condition - 身体状况
     */
    public String getPhysicalCondition() {
        return physicalCondition;
    }

    /**
     * 设置身体状况
     *
     * @param physicalCondition 身体状况
     */
    public void setPhysicalCondition(String physicalCondition) {
        this.physicalCondition = physicalCondition;
    }

    /**
     * 获取婚姻状况
     *
     * @return marital_status - 婚姻状况
     */
    public String getMaritalStatus() {
        return maritalStatus;
    }

    /**
     * 设置婚姻状况
     *
     * @param maritalStatus 婚姻状况
     */
    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    /**
     * 获取医疗费别
     *
     * @return medical_care - 医疗费别
     */
    public String getMedicalCare() {
        return medicalCare;
    }

    /**
     * 设置医疗费别
     *
     * @param medicalCare 医疗费别
     */
    public void setMedicalCare(String medicalCare) {
        this.medicalCare = medicalCare;
    }

    /**
     * 获取医保定点医院
     *
     * @return hospital - 医保定点医院
     */
    public String getHospital() {
        return hospital;
    }

    /**
     * 设置医保定点医院
     *
     * @param hospital 医保定点医院
     */
    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    /**
     * 获取商业健康保险
     *
     * @return insurance - 商业健康保险
     */
    public String getInsurance() {
        return insurance;
    }

    /**
     * 设置商业健康保险
     *
     * @param insurance 商业健康保险
     */
    public void setInsurance(String insurance) {
        this.insurance = insurance;
    }

    /**
     * 获取过敏_药物
     *
     * @return allergy_drug - 过敏_药物
     */
    public String getAllergyDrug() {
        return allergyDrug;
    }

    /**
     * 设置过敏_药物
     *
     * @param allergyDrug 过敏_药物
     */
    public void setAllergyDrug(String allergyDrug) {
        this.allergyDrug = allergyDrug;
    }

    /**
     * 获取过敏_其他
     *
     * @return allergy_others - 过敏_其他
     */
    public String getAllergyOthers() {
        return allergyOthers;
    }

    /**
     * 设置过敏_其他
     *
     * @param allergyOthers 过敏_其他
     */
    public void setAllergyOthers(String allergyOthers) {
        this.allergyOthers = allergyOthers;
    }
}