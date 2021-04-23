package com.example.demo.bean;

import lombok.Data;

import java.util.Date;

/**
 * @Author Jyo
 * @Date 2020/3/20
 */
@Data
public class Agency {

    private int agencyId;

    private String agencyName;

    private String agencyAddress;

    private String agencyPhone;

    private String agencyEmail;

    private String agencyLicenseNum;

    private String agencyLicensePic1;

    //备用营业执照照片
    private String agencyLicensePic2;

    private String legalPersonName;

    private String legalPersonPhone;

    private int examineStatus;

    private Date registerTime;

    private String modifyComments;//审核不通过修改意见

    private int onlinePay;//线上支付1 or 线下支付2
}
