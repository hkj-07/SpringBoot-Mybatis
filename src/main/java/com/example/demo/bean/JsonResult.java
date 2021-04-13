package com.example.demo.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JsonResult<E> {
    private int code; //接口状态码
    private String massage; //接口返回消息2、User.java
    private E content; //响应内容
}
