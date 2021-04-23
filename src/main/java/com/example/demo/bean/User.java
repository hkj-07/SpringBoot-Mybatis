package com.example.demo.bean;

import lombok.Data;

import java.util.Date;

@Data //注解自动getter+setter
public class User {
    private int id;

    private String userName;

    private String password;

    private int teaCompanyId;

    private int agencyId;

    private int adminId;

    private String userPassword;

    private String userEmail;

    private String userPhone;

    private Date registerTime;

    private String userAddress;

    private int userType;   //1为茶企，2为检测机构，3为管理员
}
