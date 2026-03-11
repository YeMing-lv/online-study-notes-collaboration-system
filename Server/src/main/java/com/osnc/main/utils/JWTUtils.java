package com.osnc.main.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class JWTUtils {

    private static final long expire_time = 1000 * 60 * 60 * 24;

    private static final String security_key = "MD9**+4MG^EG79RV+T?J87AI4NWQVT^&";

    public static String createToken(String username) {
        //1.当前的时间  什么时候生成的
        long cru_time = System.currentTimeMillis();
        //2.过期的时间（当前时间+有效时间）
        Date date = new Date(cru_time + expire_time);

        //3.生成一个具有有效期和用户信息声明的令牌
        // 生成加密KEY              私钥和加密算法
        Algorithm algorithm = Algorithm.HMAC256(security_key);
        //返回  生成的token
        return JWT.create()
                .withClaim("username", username) //4.用户信息 简单一点 用户名  // 前端输入的用户名
                .withExpiresAt(date) // jwt过期时间
                .sign(algorithm);  //使用sign方法对JWT进行签名，并将签名后的JWT保存在token变量中
    }

    /**
     * 验证token是否正确  不知道用户名   传入token
     * Algorithm algorithm = Algorithm.HMAC256(password) 是对密码进行加密后再与用户名混淆在一起
     * 在签名时可以通过 .withExpiresAt(date) 指定token的过期时间
     * @param token
     * @return
     */
    public static boolean Verify(String token) throws SignatureVerificationException {
        //加密KEY
        try {
            Algorithm algorithm = Algorithm.HMAC256(security_key);
            JWTVerifier jwtVerifier = JWT.require(algorithm).build();
            //调用方法 验证token是否正确
            jwtVerifier.verify(token);
        }catch (JWTDecodeException e){
            log.info("校验错误");
            return  false;
        }
        return true;
    }

    /**
     * 验证token是否正确，给定用户名和token
     * @param token 要验证的token
     * @param username 用户名
     * @return 如果token有效且属于指定的用户名，返回true；否则返回false
     */
    public static boolean Verify(String token, String username){
        // 使用HMAC256算法和密钥生成算法实例
        try {
            Algorithm algorithm = Algorithm.HMAC256(security_key);
            // 创建JWTVerifier实例，设置要验证的声明（这里是username）
            JWTVerifier jwtVerifier = JWT.require(algorithm)
                    .withClaim("username", username)
                    .build();
            // 调用 verify 方法验证token是否正确
            jwtVerifier.verify(token);
        }catch (JWTDecodeException e){
            log.info("校验错误");
            return  false; // JWT解码异常，返回false表示验证失败
        }
        return true; // token验证通过，返回true
    }

    /**
     * 得到用户的信息   传入token  解码
     * @param token
     * @return
     */
    public static String getTokenUserInfo(String token){
        //初始值为空  获取到值再存储到该变量
        String userInfo="";
        //解码
        DecodedJWT decodedJWT = JWT.decode(token);
        //根据用户标识（key）去获取数据   之前写的生成token的方法    .withClaim("username", username)
        // 这个用户标识特别重要  不要暴露出来，增加安全性
        //token里面最好不要放用户的重要机密信息，一但被decode后，很容易得到这个重要的机密信息，特别用户的登录密码，身份证之类的信息
        userInfo= decodedJWT.getClaim("username").asString();
        //返回 用户名信息
        return  userInfo;
    }

    /**
     * 根据token值获取过期时间   为什么？因为根据情况我们可以 续时
     * @param token
     * @return
     */
    public static Long getExpireTime(String token){
        //解码获取 token的信息
        DecodedJWT decode = JWT.decode(token);
        //exp是获取过期时间的key
        Claim exp = decode.getClaim("exp");
        Date date = exp.asDate();
        long time = date.getTime();
        //返回 过期时间
        return  time;
    }

    /**
     * 判断JWT是否过期
     * @param token JWT token
     * @return true(过期)，false(未过期)
     */
    public static boolean isJwtExpired(String token) {
        try {
            // 解析JWT token
            DecodedJWT jwt = JWT.decode(token);
            // 获取token的过期时间
            Date expiration = jwt.getExpiresAt();
            // 如果过期时间不为null且在当前时间之前，则表示过期
            return expiration != null && expiration.before(new Date());
        } catch (JWTVerificationException exception) {
            // Token验证异常，可视为过期
            return true;
        }
    }


//    public static void main(String[] args) {
//        String token= JWTUtils.createToken("admin");
//        //String token= eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2OTI4OTg4MjksInVzZXJuYW1lIjoi6Ieq55Sx5aaC6aOOIn0.O23nZ597-yFUV0cGuBjQy6eeBmQ1_x4vlBWzgOuM49o
//        //String token="eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE3MTAyMzI2ODEsInVzZXJuYW1lIjoiYWRtaW4ifQ.cshpvHs9RzwmaJkWQVFvv5AOtwLRaSxATKJXsPJq2M8";
//        System.out.println("生成token："+token);
//        System.out.println("校验token："+ JWTUtils.Verify(token));
//        System.out.println("校验admin用户的token是否正确："+ JWTUtils.Verify(token,"admin"));
//        System.out.println("得到用户信息:"+ JWTUtils.getTokenUserInfo(token));
//        System.out.println("得到过期时间:"+ JWTUtils.getExpireTime(token));
//
//    }
}
