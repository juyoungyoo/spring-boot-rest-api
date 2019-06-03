package com.juyoung.restapiwithspring.configs;

import com.juyoung.restapiwithspring.accounts.AccountService;
import com.juyoung.restapiwithspring.common.BaseControllerTest;
import com.juyoung.restapiwithspring.common.TestDescription;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthServerConfigTest extends BaseControllerTest {

    @Autowired
    AppProperties appProperties;
    @Autowired
    AccountService accountService;

    @Test
    @TestDescription("인증 토큰을 발급 받는 테스트") // Grant type : password and refresh token
    public void getAuthToken() throws Exception {
        // third party app이기 때문에, facebook나 google등 서버에게 redirection 발생
        mockMvc.perform(post("/oauth/token")
                        .with(httpBasic(appProperties.getClientId(),appProperties.getClientSecret())) // basic auth 생성
                        .param("username", appProperties.getUserUsername())
                         .param("password", appProperties.getUserPassword())
                        .param("grant_type", "password")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("access_token").exists())
                .andExpect(jsonPath("token_type").value("bearer"))
                .andExpect(jsonPath("scope").value("read write"))
        ;
    }
}