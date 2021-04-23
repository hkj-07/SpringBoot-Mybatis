package com.example.demo.bean;

import lombok.Data;

import java.util.Date;

/**
 * @Author Jyo
 * @Date 2020/3/20
 */
@Data
public class Admin {
    private int adminId;

    private String adminName;

    private String adminPassword;

    private String adminPhone;

    private Date registerTime;
}
