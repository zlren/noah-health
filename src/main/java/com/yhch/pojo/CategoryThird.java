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

    @Column(name = "en_short")
    private String enShort;

    @Column(name = "reference_value")
    private String referenceValue;

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
     * @return en_short
     */
    public String getEnShort() {
        return enShort;
    }

    /**
     * @param enShort
     */
    public void setEnShort(String enShort) {
        this.enShort = enShort;
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
}