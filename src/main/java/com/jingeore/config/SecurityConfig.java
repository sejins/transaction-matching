package com.jingeore.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter{

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().mvcMatchers("/","/login","/sign-up","/check-email-token").permitAll()
                .anyRequest().authenticated();
        // /, /login , /sign-up , /check-email-token 의 POST, GET 요청에 대해서는 인증을 필요로 하지 않도록 설정.
    }
}
