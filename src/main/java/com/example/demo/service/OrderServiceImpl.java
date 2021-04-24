package com.example.demo.service;

import com.example.demo.bean.Order;
import com.example.demo.mapper.OrderMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class OrderServiceImpl implements OrderService{
    @Resource
    OrderMapper orderMapper;
    @Override
    public int insert(Order order) {
        return orderMapper.insertOrder(order);
    }
}
