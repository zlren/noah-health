package com.yhch.pojo;

import javax.persistence.*;

@Table(name = "category_third")
public class CategoryThird {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "second_id")
    private Integer secondId;

    private String name;

    @Column(name = "system_category")
    private String systemCategory;

    @Column(name = "reference_value")
    private String referenceValue;

    private String hospital;

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
     * @return system_category
     */
    public String getSystemCategory() {
        return systemCategory;
    }

    /**
     * @param systemCategory
     */
    public void setSystemCategory(String systemCategory) {
        this.systemCategory = systemCategory;
    }

    /**
     * @return reference_value
     */
    public String getReferenceValue() {
        return referenceValue;
    }

    /**
     * @param referenceValue
     */
    public void setReferenceValue(String referenceValue) {
        this.referenceValue = referenceValue;
    }

    /**
     * @return hospital
     */
    public String getHospital() {
        return hospital;
    }

    /**
     * @param hospital
     */
    public void setHospital(String hospital) {
        this.hospital = hospital;
    }
}