package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RedisDemoController {
    /**
     * 注入响应式的ReactiveRedisTemplate
     */
    @Autowired
    private StringRedisTemplate redisTemplate;

    @RequestMapping(value = "/getval", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public void getredis() {
        redisTemplate.opsForValue().set("a","bbb");
        String name= redisTemplate.opsForValue().get("zxt");
        System.out.println("name="+name);
    }
}
