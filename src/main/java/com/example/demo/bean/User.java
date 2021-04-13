package com.example.demo.bean;

import lombok.Data;

@Data //注解自动getter+setter
public class User {
    private int id;
    private String userName;
    private String password;
}
