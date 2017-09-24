package com.noahhealth.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "result_health")
public class ResultHealth {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "second_id")
    private Integer secondId;

    private Date time;

    @Column(name = "upload_time")
    private Date uploadTime;

    @Column(name = "inputer_id")
    private Integer inputerId;

    @Column(name = "checker_id")
    private Integer checkerId;

    private String status;

    private String note;

    private String reason;

    private String content;

    @Column(name = "content_new")
    private String contentNew;

    private String problem;

    @Column(name = "problem_new")
    private String problemNew;
}