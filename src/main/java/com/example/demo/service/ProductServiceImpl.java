package com.example.demo.service;

import com.example.demo.bean.Product;
import com.example.demo.mapper.ProductMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ProductServiceImpl implements ProductService{
    @Resource
    ProductMapper productMapper;
    @Override
    public Product selectById(long id) {
        return productMapper.selectById(id);
    }

    @Override
    public int seckill(long id) {
        return productMapper.seckillById(id);
    }
}
