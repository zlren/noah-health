package com.yhch.pojo;

import javax.persistence.*;

@Table(name = "category_second")
public class CategorySecond {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "first_id")
    private Integer firstId;

    private String name;

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
     * @return first_id
     */
    public Integer getFirstId() {
        return firstId;
    }

    /**
     * @param firstId
     */
    public void setFirstId(Integer firstId) {
        this.firstId = firstId;
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
}