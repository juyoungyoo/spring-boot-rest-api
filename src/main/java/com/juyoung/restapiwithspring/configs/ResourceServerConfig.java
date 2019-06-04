package com.juyoung.restapiwithspring.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;

@Configuration
@EnableResourceServer
// 리소스 접근이 필요할 때, token service auth server에서 토큰을 확인한다 인증 정보가 있는지 없는지 확인, 접근 제한을 한다.
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId("event");
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
            .anonymous()
                .and()
            .authorizeRequests()
                .mvcMatchers( HttpMethod.GET, "/api/**").permitAll()
                .mvcMatchers( HttpMethod.POST, "/api/**").authenticated()
                .mvcMatchers( HttpMethod.PUT, "/api/**").authenticated()
            .and()
            .exceptionHandling()   // 인증 이 잘못된경우, 권한이 잘못된 경우
                .accessDeniedHandler(new OAuth2AccessDeniedHandler()); // 접근권한이 없는 경우에 exception을 하겠다
    }
}

