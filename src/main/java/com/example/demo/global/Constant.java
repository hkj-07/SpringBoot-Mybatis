package com.example.demo.global;

public class Constant {
    public static final int FAIL_CODE = 500;

    public static final int FAIL_CODE2 = 201;

    public static final int FAIL_CODE3 = 202;

    public static final int FAIL_CODE4 = 203;

    //接口调用成功
    public static final int SUCCESS_CODE = 200;

    //用户类型，2为检测机构
    public static final int USER_AGENCY_TYPE = 2;

    //用户类型，1为茶企
    public static final int USER_COMPANY_TYPE = 1;

    //用户类型，3为管理员
    public static final int USER_ADMIN_TYPE = 3;

    //用户名或密码错误
    public static final int LOGIN_CODE_E01 = 205;

    //用户不存在
    public static final int LOGIN_CODE_E02 = 206;

    //权限验证失败
    public static final int AUTH_FAIL_CODE_E1 = 401;

    //权限验证失败，重新生成token
    public static final int AUTH_FAIL_CODE_E2 = 402;



    //设置accessToken过期时间，单位：毫秒
    public static final long ACCESS_TOKEN_EXPIRED_TIME = 30 * 60 * 1000;

    //设置refreshToken过期时间，单位：毫秒
//    public static final long REFRESH_TOKEN_EXPIRED_TIME = 3 * 60 * 1000;
    public static final long REFRESH_TOKEN_EXPIRED_TIME = 5 * 24 * 60 * 60 * 1000;

    // 存入redis中的accessToken的键的前缀
    public static final String ACCESS_TOKEN_KEY = "ACCESS-TOKEN-USER-";
}
