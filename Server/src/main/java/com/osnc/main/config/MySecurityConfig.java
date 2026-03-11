package com.osnc.main.config;

import com.osnc.main.filter.JwtFilter;
import com.osnc.main.service.impl.UserServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity//开启springsecurity配置修改
public class MySecurityConfig {

    @Resource
    @Lazy
    UserServiceImpl userService;

    @Resource
    JwtFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userService);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .formLogin(AbstractHttpConfigurer::disable)//取消默认登录页面的使用
                .logout(AbstractHttpConfigurer::disable)//取消默认登出页面的使用
                .authenticationProvider(authenticationProvider())//将自己配置的PasswordEncoder放入SecurityFilterChain中
                .csrf(AbstractHttpConfigurer::disable)//禁用csrf保护，前后端分离不需要
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))//禁用session，因为我们已经使用了JWT
                .httpBasic(AbstractHttpConfigurer::disable)//禁用httpBasic，因为我们传输数据用的是post，而且请求体是JSON
                .authorizeHttpRequests(request -> request.requestMatchers(HttpMethod.POST, "/user/login", "/user/register", "/checkToken").permitAll()
                        .requestMatchers(HttpMethod.GET, "/image/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/ws-endpoint1/**").permitAll()
                        .requestMatchers("/ws-native/**").permitAll()
                        .requestMatchers("/sockjs/**").permitAll()
                        .anyRequest().authenticated())//开放接口
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);//将用户授权时用到的JWT校验过滤器添加进SecurityFilterChain中，并放在UsernamePasswordAuthenticationFilter的前面
        return httpSecurity.build();
    }

}
