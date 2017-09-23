package com.yhch.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "result_input")
public class ResultInput {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "second_id")
    private Integer secondId;

    private String hospital;

    @Column(name = "upload_time")
    private Date uploadTime;

    private Date time;

    @Column(name = "inputer_id")
    private Integer inputerId;

    @Column(name = "checker_id")
    private Integer checkerId;

    private String status;

    private String note;

    private String reason;
}