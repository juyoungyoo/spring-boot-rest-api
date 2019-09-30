package com.juyoung.restapiwithspring.common;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.juyoung.restapiwithspring.accounts.Account;
import com.juyoung.restapiwithspring.accounts.AccountService;
import com.juyoung.restapiwithspring.accounts.RoleType;
import com.juyoung.restapiwithspring.configs.AppSecurityProperties;
import jdk.nashorn.internal.ir.annotations.Ignore;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.oauth2.common.util.Jackson2JsonParser;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.Filter;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.web.reactive.function.client.ExchangeFilterFunctions.basicAuthentication;

@Ignore
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@Slf4j
public class BaseControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected ModelMapper modelMapper;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private FilterChainProxy filterChainProxy;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AppSecurityProperties appSecurityProperties;

    @BeforeEach
    public void setUp(RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .addFilters(filterChainProxy)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    protected ResultActions getResource(String url, Object... pathVariables) throws Exception {
        return mockMvc.perform(RestDocumentationRequestBuilders.get(url, pathVariables)
                .header(HttpHeaders.AUTHORIZATION, "bearer " + getAccessToken(false))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON));
    }

    protected ResultActions getResources(String url, MultiValueMap parameters) throws Exception {
        return mockMvc.perform(RestDocumentationRequestBuilders.get(url)
                .header(HttpHeaders.AUTHORIZATION, "bearer " + getAccessToken(false))
                .params(parameters)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON));
    }

    protected <T> ResultActions postResource(String url, T body) throws Exception {
        return mockMvc.perform(RestDocumentationRequestBuilders.post(url)
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(body)));
    }

    protected <T> ResultActions putResource(T body, String url, Object... pathVariables) throws Exception {
        return mockMvc.perform(RestDocumentationRequestBuilders.put(url, pathVariables)
                .header(HttpHeaders.AUTHORIZATION, "bearer " + getAccessToken(false))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(body)));
    }

    protected <T> ResultActions deleteResource(String url, T body) throws Exception {
        return mockMvc.perform(delete(url)
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON));
    }

    private String getBearerToken() throws Exception {
        return "bearer " + getAccessToken(true);
    }

    private String getAccessToken(boolean needToAccount) throws Exception {
        if (needToAccount) {
            createAccount();
        }

        ResultActions perform = mockMvc.perform(post("/oauth/token")
                .with(httpBasic(appSecurityProperties.getClientId(), appSecurityProperties.getClientSecret())) // basic auth 생성
                .param("username", appSecurityProperties.getUserUsername())
                .param("password", appSecurityProperties.getUserPassword())
                .param("grant_type", "password"));

        String response = perform.andReturn().getResponse().getContentAsString();
        Jackson2JsonParser parser = new Jackson2JsonParser();
        return parser.parseMap(response).get("access_token").toString();
    }

    private Account createAccount() {
        return this.accountService.signIn(getAccount());
    }

    private Account getAccount() {
        return Account.builder()
                .email(appSecurityProperties.getUserUsername())
                .password(appSecurityProperties.getUserPassword())
                .roles(Arrays.stream(RoleType.values()).collect(Collectors.toSet()))
                .build();
    }
}
