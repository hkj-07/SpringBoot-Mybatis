package com.example.demo.Utils;


import io.jsonwebtoken.*;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author Hkj
 * @Date 2020/3/28
 */
@Component
public class JwtUtils {
    //设置Token过期时间，单位：毫秒
    private static final long TOKEN_EXPIRED_TIME = 1 * 24 * 60 * 60 * 1000;

//    private static final long TOKEN_EXPIRED_TIME = 60*1000;
    /**
     * 根据userPhone和userType生成token
     */
    public static String generateToken(String userPhone, Integer userType) {
        Map<String, Object> map = new HashMap<>();
        map.put("userPhone", userPhone);
        map.put("userType", userType);
        return buildJwt(map, TOKEN_EXPIRED_TIME);
    }
    //设置jwtId
    private static String JWT_ID = "JWT_ID";

    //设置密钥
    private static String security;

    @Value("${token.security}")
    public void setSecurity(String security){
        this.security = security;
    }


    /**
    * 创建JWT
    */
    public static String buildJwt(Map<String, Object> claims, Long time){
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;   //指定签名算法
        Date now = new Date();

        SecretKey secretKey = generalKey();

        long nowMillis = System.currentTimeMillis();//生成JWT的时间
        //在payload添加声明
        JwtBuilder builder = Jwts.builder()
                .setClaims(claims)          //设置私有声明
                .setId(JWT_ID)                  //设置jti(JWT ID)：是JWT的唯一标识
                .setIssuedAt(now)           //iat: jwt的签发时间
                .signWith(signatureAlgorithm, secretKey);//设置签名使用的签名算法和签名使用的秘钥
        if (time >= 0) {
            long expMillis = nowMillis + time;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);     //设置过期时间
        }
        return builder.compact();
    }

    /**
     * 验证jwt
     */
    public static Claims verifyJwt(String token) {
        //签名秘钥
        SecretKey key = generalKey();
        Claims claims;
        if(token == null){
            return null;
        }
        try {
            claims = Jwts.parser()  //得到DefaultJwtParser
                    .setSigningKey(key)         //设置签名的秘钥
                    .parseClaimsJws(token).getBody();

            String userPhone = (String)claims.get("userPhone");

            if (userPhone == null || StringUtils.isEmpty(userPhone)) {
                return null;
            }

        } catch (SignatureException | MalformedJwtException e) {
            //解析异常
            return null;
        } catch(ExpiredJwtException e) {
            //过期异常
            return null;
        }
        return claims;
    }


    /**
     * 由字符串生成加密key
     *
     * @return
     */
    public static SecretKey generalKey() {
        byte[] encodedKey = Base64.decodeBase64(security);
        SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
        return key;
    }



}
