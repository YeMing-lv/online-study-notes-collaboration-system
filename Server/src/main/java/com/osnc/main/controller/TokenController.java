package com.osnc.main.controller;

import com.osnc.main.common.Result;
import com.osnc.main.utils.JWTUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TokenController {
    /**
     * 检查token
     * 只知道token
     * @param request
     * @return Boolean
     */
    @PostMapping("/checkToken")
    public Result checkToken(HttpServletRequest request){
        System.out.println("前端访问页面验证token");
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (token == null || token == "") {
            return Result.failure("token为空");
        }
        if (JWTUtils.Verify(token)) {
            return Result.success(true);
        }
        return Result.failure("token无效");
    }
}
