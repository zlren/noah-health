package com.yhch.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
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

}