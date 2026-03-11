package com.osnc.main.filter;

import com.osnc.main.service.impl.UserServiceImpl;
import com.osnc.main.utils.JWTUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    @Resource
    @Lazy
    UserServiceImpl userService;

    // 定义需要放行的WebSocket相关路径
//    private static final List<String> EXCLUDED_PATHS = Arrays.asList(
//            "/ws-endpoint1/",    // WebSocket端点根路径
//            "/sockjs/"           // SockJS相关请求路径
//    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        String requestURI = request.getRequestURI();
//        log.info("Request API: "+ requestURI);
//
//        // ========== 核心修复：放行WebSocket相关请求 ==========
//        if (isExcludedPath(requestURI)) {
//            filterChain.doFilter(request, response);
//            return; // 直接放行，不执行后续JWT校验
//        }

        String jwtToken = request.getHeader(HttpHeaders.AUTHORIZATION);//从请求头中获取token
        log.info("Request API: "+ request.getRequestURI());
        if (jwtToken != null && JWTUtils.Verify(jwtToken)){
            try {//token可用
                Long cur_time =System.currentTimeMillis();
                //获取过时的时间  调用工具类
                Long exp_time = JWTUtils.getExpireTime(jwtToken);

                Long time =(exp_time-cur_time)/(1000*60);
                //校验  token 小于十五分钟 就过期 此时对toekn进行续时处理
                if(time>0  && time <240){
                    //调用工具类 工具目前还没过期的token  获取到用户信息 （即  用户名）
                    String userinfo = JWTUtils.getTokenUserInfo(jwtToken);
                    //根据 用户名  生成新的 token
                    String new_token = JWTUtils.createToken(userinfo);
                }

                String username = JWTUtils.getTokenUserInfo(jwtToken); // 获取用户姓名
                UserDetails user = userService.loadUserByUsername(username);
                if (user != null){
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, null);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (Exception e){
                log.error(e.getMessage());
            }
        }else {
            log.warn("token is null or empty or out of time, probably user is not log in !");
        }
        filterChain.doFilter(request, response);//继续过滤
    }

//    // 辅助方法：判断是否为需要放行的路径
//    private boolean isExcludedPath(String requestURI) {
//        for (String excludedPath : EXCLUDED_PATHS) {
//            if (requestURI.startsWith(excludedPath)) {
//                return true;
//            }
//        }
//        return false;
//    }

}
