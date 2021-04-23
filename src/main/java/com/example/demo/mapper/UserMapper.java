package com.example.demo.mapper;

import com.example.demo.bean.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper {
    User login(User user);

    User userLogin(User user);

//    List<User> selectByNameOrEmailOrPhone(String userName, String userEmail, String userPhone);

//    Boolean userRegister(User user);
}
