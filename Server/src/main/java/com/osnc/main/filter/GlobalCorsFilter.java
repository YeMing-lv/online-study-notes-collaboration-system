package com.osnc.main.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

// 最高优先级（比所有过滤器都先执行）
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalCorsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse res = (HttpServletResponse) response;
        HttpServletRequest req = (HttpServletRequest) request;

        // 1. 允许前端域名（必须精准）
        res.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        // 2. 允许携带凭证（cookie/token）
        res.setHeader("Access-Control-Allow-Credentials", "true");
        // 3. 允许的请求头
        res.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, Accept, Origin");
        // 4. 允许的请求方法
        res.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        // 5. 预检请求缓存时间
        res.setHeader("Access-Control-Max-Age", "3600");
        // 6. 暴露响应头（确保浏览器能读取）
        res.setHeader("Access-Control-Expose-Headers", "Access-Control-Allow-Origin");

        // 处理OPTIONS预检请求（直接返回200）
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            res.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        // 继续执行过滤器链
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void destroy() {}
}