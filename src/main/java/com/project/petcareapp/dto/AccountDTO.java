package com.project.petcareapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountDTO implements Serializable {
    private int id;
        private String password;
    private Integer roleId;
    private String username;
    private String fullname;
    private String email;
    private String phone;
    private String gender;
    private String address;
    private String createdTime;
    private String updatedTime;
    private String token;
    private String message;

}
