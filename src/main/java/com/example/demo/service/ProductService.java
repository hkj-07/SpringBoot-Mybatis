package com.example.demo.service;

import com.example.demo.bean.Product;

public interface ProductService {
    Product selectById(long id);

    int seckill(long id);
}
