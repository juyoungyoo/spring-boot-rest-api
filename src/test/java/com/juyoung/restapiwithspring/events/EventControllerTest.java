/*
package com.juyoung.restapiwithspring.events;

import com.juyoung.restapiwithspring.accounts.Account;
import com.juyoung.restapiwithspring.accounts.AccountRepository;
import com.juyoung.restapiwithspring.accounts.AccountService;
import com.juyoung.restapiwithspring.accounts.RoleType;
import com.juyoung.restapiwithspring.common.BaseControllerTest;
import com.juyoung.restapiwithspring.common.DisplayName;
import com.juyoung.restapiwithspring.configs.AppSecurityProperties;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.security.oauth2.common.util.Jackson2JsonParser;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class EventControllerTest extends BaseControllerTest {

    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    AppSecurityProperties appSecurityProperties;

    // travis test
    @Before
    public void setUp() throws Exception {
        eventRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    @DisplayName("이벤트 수정 성공")
    public void updateEvent() throws Exception {
        String eventName = "Update name";

        Event originEvent = generateEvent(1, createAccount());
        EventDto.CreateOrUpdate newEvent = modelMapper.map(originEvent, EventDto.CreateOrUpdate.class);
        newEvent.setName(eventName);

        mockMvc.perform(put("/api/events/{id}", originEvent.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearerToken(false))
                .content(objectMapper.writeValueAsString(newEvent))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(jsonPath("name").value(newEvent.getName()))
                .andExpect(jsonPath("id").value(originEvent.getId()))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("update-event",
                        getRequestFieldsSnippet())
                )
        ;
    }

    @Test
    @DisplayName("입력한 데이터가 이상한 경우 400 에러")
    public void updateEvent_Data_Empty_400() throws Exception {
        EventDto.CreateOrUpdate eventDto = createEventDto();
        eventDto.setName(null);

        mockMvc.perform(put("/api/events/234")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .content(objectMapper.writeValueAsString(eventDto))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @DisplayName("입력한 데이터가 이상한 경우 400 에러")
    public void updateEvent400() throws Exception {
        Event originEvent = generateEvent(1, createAccount());
        EventDto.CreateOrUpdate eventDto = modelMapper.map(originEvent, EventDto.CreateOrUpdate.class);
        eventDto.setName("nonEvent");
        eventDto.setBasePrice(600);
        eventDto.setMaxPrice(500);

        mockMvc.perform(put("/api/events/{id}", originEvent.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearerToken(false))
                .content(objectMapper.writeValueAsString(eventDto))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("수정하려는 이벤트가 없는 경우 404 에러")
    public void updateEvent404() throws Exception {
        Event originEvent = generateEvent(1, createAccount());
        EventDto.CreateOrUpdate eventDto = modelMapper.map(originEvent, EventDto.CreateOrUpdate.class);
        eventDto.setName("nonEvent");

        mockMvc.perform(put("/api/events/234324")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken(false))
                .content(objectMapper.writeValueAsString(eventDto))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("없는 이벤트를 조회했을 때 404 에러")
    public void getEvent404() throws Exception {
        mockMvc.perform(get("/api/events/324124"))
                .andExpect(status().isNotFound());
    }
    @Test
    @DisplayName("기존의 이벤트 하나 조회한다")
    public void getEvent() throws Exception {
        Event event = generateEvent(100);

        mockMvc.perform(get("/api/events/{id}", event.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("get-an-event"))
        ;
    }



    @Test
    @DisplayName("30개의_이벤트를_10개씩_두번째_페이지_조회하기")
    public void queryEvents() throws Exception {
        createAccount();
        // Given
        IntStream.range(0, 30).forEach(this::generateEvent);
        // When
        ResultActions perform = mockMvc.perform(get("/api/events")
                .param("page", "1")
                .param("size", "10")
                .param("sort", "name,DESC"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("query-events"));
    }


    @Test
    @DisplayName("30개의_이벤트를_10개씩_두번째_페이지_조회하기")
    public void queryEventsWithAuthorization() throws Exception {
        // Given
        IntStream.range(0, 30).forEach(this::generateEvent);
        // When
        ResultActions perform = mockMvc.perform(get("/api/events")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .param("page", "1")
                .param("size", "10")
                .param("sort", "name,DESC"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andExpect(jsonPath("_links.create-event").exists())
                .andDo(document("query-events"));
    }


    private EventDto.CreateOrUpdate createEventDto() {
        return EventDto.CreateOrUpdate.builder()
                    .name("Spring")
                    .description("REST API Development with Spring")
                    .beginEnrollmentDateTime(LocalDateTime.of(2019, 4, 2, 23, 57))
                    .closeEnrollmentDateTime(LocalDateTime.of(2019, 4, 3, 23, 57))
                    .beginEventDateTime(LocalDateTime.of(2019, 4, 4, 23, 57))
                    .endEventDateTime(LocalDateTime.of(2019, 4, 5, 23, 57))
                    .basePrice(100)
                    .maxPrice(200)
                    .limitOfEnrollment(100)
                    .location("강남역 D2 startup factory")
                    .build();
    }

    private Event generateEvent(int i) {
        return generateEvent(i, getAccount());
    }

    private Event generateEvent(int i, Account account) {
        Event event = modelMapper.map(createEventDto(), Event.class);
        event.setManager(account);
        return eventRepository.save(event);
    }

    private String getBearerToken() throws Exception {
        return "bearer " + getAccessToken(true);
    }

    private String getBearerToken(boolean needToAccount) throws Exception {
        return "bearer " + getAccessToken(needToAccount);
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
        return this.accountService.saveAccount(getAccount());
    }

    private Account getAccount() {
        return Account.builder()
                    .email(appSecurityProperties.getUserUsername())
                    .password(appSecurityProperties.getUserPassword())
                    .roles(Arrays.stream(RoleType.values()).collect(Collectors.toSet()))
                    .build();
    }

    private ResponseFieldsSnippet getResponseFieldsSnippet() {
        return relaxedResponseFields(
                fieldWithPath("id").description("identifier of new event"),
                fieldWithPath("name").description("name of new event"),
                fieldWithPath("description").description("description of new event"),
                fieldWithPath("beginEnrollmentDateTime").description("date time of begin of new event"),
                fieldWithPath("closeEnrollmentDateTime").description("date time of close of new event"),
                fieldWithPath("beginEventDateTime").description("date time of begin of new event"),
                fieldWithPath("endEventDateTime").description("date time of close of new event"),
                fieldWithPath("location").description("location of new event"),
                fieldWithPath("basePrice").description("base price of new event"),
                fieldWithPath("maxPrice").description("max price of new event"),
                fieldWithPath("limitOfEnrollment").description("limit of enrollment of new event"),
                fieldWithPath("free").description("it tells if this event free or not"),
                fieldWithPath("offline").description("it tells if this event offline meeting or not"),
                fieldWithPath("eventStatus").description("event status")
        );
    }

    private RequestFieldsSnippet getRequestFieldsSnippet() {
        return requestFields(
                fieldWithPath("name").description("name of new event"),
                fieldWithPath("description").description("description of new event"),
                fieldWithPath("beginEnrollmentDateTime").description("date time of begin of new event"),
                fieldWithPath("closeEnrollmentDateTime").description("date time of close of new event"),
                fieldWithPath("beginEventDateTime").description("date time of begin of new event"),
                fieldWithPath("endEventDateTime").description("date time of close of new event"),
                fieldWithPath("location").description("location of new event"),
                fieldWithPath("basePrice").description("base price of new event"),
                fieldWithPath("maxPrice").description("max price of new event"),
                fieldWithPath("limitOfEnrollment").description("limit of enrollment of new event")
        );
    }
}
*/
