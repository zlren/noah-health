package com.noahhealth.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
    private String birth;

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
}