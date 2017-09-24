package com.noahhealth.pojo;

import lombok.Data;

import javax.persistence.*;

@Data
@Table(name = "result_input_detail")
public class ResultInputDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "result_input_id")
    private Integer resultInputId;

    @Column(name = "third_id")
    private Integer thirdId;

    private String value;

    /**
     * 是否异常
     */
    private Boolean normal;
}