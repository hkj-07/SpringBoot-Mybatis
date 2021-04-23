package com.example.demo.bean;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Kafka {
    private String userName;
    private String userId;
    private String state;
}
