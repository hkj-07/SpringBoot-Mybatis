package com.example.demo.bean;

import lombok.Data;

import java.util.Date;

@Data
public class TeaCompany {

    private int teaCompanyId;

    private String teaCompanyName;

    private String teaCompanyPhone;

    private String teaCompanyEmail;

    private Date registerTime;

}
