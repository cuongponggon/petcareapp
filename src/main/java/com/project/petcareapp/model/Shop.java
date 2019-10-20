package com.project.petcareapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class Shop implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;


    @Basic
    @Column(name = "owner_id")
    private int ownerId;

    @Basic
    @Column(name = "name")
    private String name;

    @Basic
    @Column(name = "description")
    private String description;

    @Basic
    @Column(name = "address")
    private String address;

    @Basic
    @Column(name = "district_id")
    private int districtId;

    @Basic
    @Column(name = "latitude")
    private String latitude;

    @Basic
    @Column(name = "longitude")
    private String longitude;


    @Column(name = "status")
    private String status;

}

