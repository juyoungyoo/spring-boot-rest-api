package com.juyoung.restapiwithspring.configs;

import com.juyoung.restapiwithspring.accounts.AccountService;
import com.juyoung.restapiwithspring.common.BaseControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthServerConfigTest extends BaseControllerTest {

    @Autowired
    AppSecurityProperties appSecurityProperties;

    @Autowired
    AccountService accountService;

    @DisplayName("인증 토큰을 발급 받는 테스트")
    @Test
    void getAuthToken() throws Exception {
        mockMvc.perform(post("/oauth/token")
                        .with(httpBasic(appSecurityProperties.getClientId(), appSecurityProperties.getClientSecret())) // basic auth 생성
                        .param("username", appSecurityProperties.getUserUsername())
                         .param("password", appSecurityProperties.getUserPassword())
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