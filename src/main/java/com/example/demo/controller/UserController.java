package com.example.demo.controller;

import com.example.demo.bean.JsonResult;
import com.example.demo.bean.User;
import com.example.demo.global.Constant;
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
            return new JsonResult(Constant.SUCCESS_CODE, "登陆成功", res);
        }
        return new JsonResult(Constant.FAIL_CODE, "登陆失败", null);
    }
    /**
    @RequestMapping("/uerlogin")
    public ResponseJson userLogin(@RequestBody User user){

        Map<String, Object> resMap = userService.userLogin(user);
        if (resMap.get("resCode").equals(Constant.SUCCESS_CODE)){
            resMap.remove("resCode");
            return new ResponseJson(Constant.SUCCESS_CODE, resMap);
        }else if (resMap.get("resCode").equals(Constant.LOGIN_CODE_E01)){

            return new ResponseJson(Constant.LOGIN_CODE_E01, "用户名或密码错误!");
        }else {

            return new ResponseJson(Constant.LOGIN_CODE_E02, "用户不存在!");
        }
    }
    */
}
