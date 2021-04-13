package com.example.demo.controller;

import com.example.demo.bean.JsonResult;
import com.example.demo.bean.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public JsonResult login(@RequestBody User user){
        User res = userService.login(user);
        if (res != null) {
            return new JsonResult(200, "登陆成功", res);
        }
        return new JsonResult(500, "登陆失败", null);
    }
}
