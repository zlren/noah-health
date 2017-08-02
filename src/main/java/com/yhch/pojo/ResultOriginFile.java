package com.yhch.pojo;

import javax.persistence.*;

@Table(name = "result_origin_file")
public class ResultOriginFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "result_origin_id")
    private Integer resultOriginId;

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
     * @return result_origin_id
     */
    public Integer getResultOriginId() {
        return resultOriginId;
    }

    /**
     * @param resultOriginId
     */
    public void setResultOriginId(Integer resultOriginId) {
        this.resultOriginId = resultOriginId;
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