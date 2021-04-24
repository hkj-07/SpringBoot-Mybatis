package com.example.demo.Utils;

import com.example.demo.global.Constant;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.DefaultClaims;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author Jyo
 * @Date 2020/3/30
 */
@Component
public class JwtUtils {

//    private static final long TOKEN_EXPIRED_TIME = 60*1000;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public static JwtUtils jwtUtils;

    /**
     * 直接注入StringRedisTemplate无效，通过容器初始化给 StringRedisTemplate赋值
     */
    @PostConstruct
    public void init(){
        jwtUtils = this;
        jwtUtils.stringRedisTemplate = this.stringRedisTemplate;
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

        long nowMillis = System.currentTimeMillis();
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
        Claims claims = new DefaultClaims();
        if(token == null || StringUtils.isEmpty(token)){

            return null;
        }
        try {
            claims = Jwts.parser()  //得到DefaultJwtParser
                    .setSigningKey(key)         //设置签名的秘钥
                    .parseClaimsJws(token).getBody();

            int userId = (Integer)claims.get("userId");

            claims.put("isExpired", false);

            if (userId == 0) {
                //无效token
                return null;
            }

        } catch(SignatureException | MalformedJwtException e) {
            //解析异常
            return null;
        } catch(ExpiredJwtException e) {
            //过期异常
            claims.put("isExpired", true);

            return claims;
        } catch (JwtException e) {
            //JWT其他异常
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

    /**
     * 根据userId生成token
     */
    public static String generateToken(int userId, long expiredTime) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        return buildJwt(map, expiredTime);
    }

    /**
     * 生成token，并存入redis
     */
    public static Map<String, String>  creatTokenAndInsertRedis(int userId){

        String accessToken = generateToken(userId, Constant.ACCESS_TOKEN_EXPIRED_TIME); //权限验证token
        String refreshToken = generateToken(userId, Constant.REFRESH_TOKEN_EXPIRED_TIME); //刷新token

        Map<String, String> resMap = new HashMap<String, String>();
        resMap.put("accessToken", accessToken);
        resMap.put("refreshToken", refreshToken);

        //accessToken存入Redis
        jwtUtils.stringRedisTemplate.opsForValue().set(Constant.ACCESS_TOKEN_KEY + userId, accessToken);

        return resMap;
    }

}
