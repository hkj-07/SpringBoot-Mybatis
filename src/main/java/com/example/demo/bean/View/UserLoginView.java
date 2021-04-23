package com.example.demo.bean.View;

import lombok.Data;

@Data
public class UserLoginView {
    private int userId;

    private String userName;

    private String userEmail;

    private String userPhone;

    private String registerTime;

    private int userType;   //1为检测机构，2为茶企，3为管理员
}
