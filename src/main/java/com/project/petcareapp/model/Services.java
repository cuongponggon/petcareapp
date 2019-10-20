package com.project.petcareapp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Services implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Basic
    @Column(name = "service_name")
    private String serviceName;

    @Basic
    @Column(name = "service_price")
    private Float servicePrice;

    @Basic
    @Column(name = "category_id")
    private int categoryId;

    @Basic
    @Column(name = "unit")
    private String unit;

    @Basic
    @Column(name = "description")
    private String description;

    @Basic
    @Column(name = "status")
    private String status;

}


