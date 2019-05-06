package com.juyoung.restapiwithspring.configs;

import com.juyoung.restapiwithspring.accounts.Account;
import com.juyoung.restapiwithspring.accounts.AccountService;
import com.juyoung.restapiwithspring.accounts.RoleType;
import com.juyoung.restapiwithspring.common.TestDescription;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 인증 토큰 발급

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthServerConfigTest{

    @Autowired
    AccountService accountService;
    @Autowired
    MockMvc mockMvc;

    @Test
    @TestDescription("인증 토큰을 발급 받는 테스트") // Grant type : password and refresh token
    public void getAuthToken() throws Exception {
        // grant-type password 특징 : hope 요청, 응답이 한쌍으로 한번만 한다. ( 써드파트에서 사용 x, 인증정보를 보유하고 있는 곳에서만 사용해야함)
        // third party app이기 때문에, facebook나 google등 서버에게 redirection 발생
        // hope이 많음
        String clientId = "myApp";
        String clientSecret = "pass";
        String username = "juyoung@email.com";
        String password = "pass";
        Account account = Account.builder()
                            .email(username)
                            .password(password)
                            .roles(Arrays.stream(RoleType.values()).collect(Collectors.toSet()))
                            .build();

        mockMvc.perform(post("/oauth/token")
                        .with(httpBasic(clientId,clientSecret)) // basic auth 생성
                        .param("username", username)
                        .param("password", password)
                        .param("grant_type", "password")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("access_token").exists());
    }
}