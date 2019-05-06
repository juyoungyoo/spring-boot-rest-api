package com.juyoung.restapiwithspring.configs;

import com.juyoung.restapiwithspring.accounts.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    @Autowired
    AccountService accountService; // equals userdetail service
    @Autowired
    PasswordEncoder passwordEncoder;

    // token 저장소 : inmemory
    @Bean
    public TokenStore tokenStore(){
        return new InMemoryTokenStore();
    }

    // AuthenticationManager bean setting
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    // AuthenticationManager 만드는 방법

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(accountService)
                .passwordEncoder(passwordEncoder);
    }

    // filter 적용할지말지
    // web > http
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().mvcMatchers("/docs/index.html");
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    // 동일
    // todo : 사용자 계정 추가, 이메일 인증
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.anonymous()    // 익명 허용
                .and()
             .formLogin()
                .and()
             .authorizeRequests()
                .mvcMatchers(HttpMethod.GET, "/api").authenticated()
                .mvcMatchers(HttpMethod.GET, "/api/**").authenticated()
                .anyRequest().authenticated();  // 나머지는 모두 인증이 필요하다
    }
}
