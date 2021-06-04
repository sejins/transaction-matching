package com.jingeore.config;

import com.jingeore.account.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter{

    private final AccountService accountService;
    private final DataSource dataSource;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().mvcMatchers("/","/login","/sign-up","/check-email-token","/resend-email").permitAll()
                .anyRequest().authenticated();
        // /, /login , /sign-up , /check-email-token 의 POST, GET 요청에 대해서는 인증을 필요로 하지 않도록 설정.

        http.formLogin().loginPage("/login").permitAll(); // 커스텀 로그인 페이지를 사용하는 설정
        http.logout().logoutSuccessUrl("/");

        http.rememberMe().userDetailsService(accountService).tokenRepository(tokenRepository()); // remember-me 설정
    }

    @Bean
    public PersistentTokenRepository tokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
    }

    public void configure(WebSecurity webSecurity){ // 정적 파일에 대해서는 인증을 요구하지 않게 설정.
        webSecurity.ignoring().mvcMatchers("/node_modules/**") // node_modules 밑의 프론트앤드 라이브러리에 대한 요청.
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

}
