package com.example.demo.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Author Hkj
 * @Date 2020/3/11
 */
@Data
@AllArgsConstructor
public class ResponseJson<T> {
    private int code;//状态码
    private T content;//内容
}
