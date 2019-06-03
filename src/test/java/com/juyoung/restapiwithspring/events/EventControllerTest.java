package com.juyoung.restapiwithspring.events;

import com.juyoung.restapiwithspring.accounts.Account;
import com.juyoung.restapiwithspring.accounts.AccountRepository;
import com.juyoung.restapiwithspring.accounts.AccountService;
import com.juyoung.restapiwithspring.accounts.RoleType;
import com.juyoung.restapiwithspring.common.BaseControllerTest;
import com.juyoung.restapiwithspring.common.TestDescription;
import com.juyoung.restapiwithspring.configs.AppProperties;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.common.util.Jackson2JsonParser;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    AppProperties appProperties;

    @Before
    public void setUp() throws Exception {
        accountRepository.deleteAll();
        eventRepository.deleteAll();
    }

    @Test
    @TestDescription("이벤트 수정 성공")
    public void updateEvent() throws Exception {
        String eventName = "Update name";
        Event originEvent = generateEvent(1);
        EventDto expectEvent = modelMapper.map(originEvent, EventDto.class);
        expectEvent.setName(eventName);

        mockMvc.perform(put("/api/events/{id}", originEvent.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .content(objectMapper.writeValueAsString(expectEvent))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(jsonPath("name").value(expectEvent.getName()))
                .andExpect(jsonPath("id").value(originEvent.getId()))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("update-event"))
                ;
    }

    @Test
    @TestDescription("입력한 데이터가 이상한 경우 400 에러")
    public void updateEvent_Data_Empty_400() throws Exception {
        EventDto eventDto = EventDto.builder().build();
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
    @TestDescription("입력한 데이터가 이상한 경우 400 에러")
    public void updateEvent400() throws Exception {
        Event originEvent = generateEvent(1);
        EventDto eventDto = modelMapper.map(originEvent, EventDto.class);
        eventDto.setName("nonEvent");
        eventDto.setBasePrice(600);
        eventDto.setMaxPrice(500);

        mockMvc.perform(put("/api/events/{id}", originEvent.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .content(objectMapper.writeValueAsString(eventDto))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @TestDescription("수정하려는 이벤트가 없는 경우 404 에러")
    public void updateEvent404() throws Exception {
        Event originEvent = generateEvent(1);
        EventDto eventDto = modelMapper.map(originEvent, EventDto.class);
        eventDto.setName("nonEvent");

        mockMvc.perform(put("/api/events/234324")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .content(objectMapper.writeValueAsString(eventDto))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @TestDescription("없는 이벤트를 조회했을 때 404 에러")
    public void getEvent404() throws Exception {
        mockMvc.perform(get("/api/events/324124"))
                .andExpect(status().isNotFound());
    }

    @Test
    @TestDescription("기존의 이벤트 하나 조회한다")
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
    @TestDescription("30개의_이벤트를_10개씩_두번째_페이지_조회하기")
    public void queryEvents() throws Exception {
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

    private Event generateEvent(int i) {
        Event event = Event.builder()
                .name("event " + i)
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
        return eventRepository.save(event);
    }


    @Test
    @TestDescription("입력값이 잘못 들어온 경우 에러가 발생하는 테스트")
    public void createEnvent_Bad_Reequest_Wrong_Input() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2019, 4, 5, 23, 57))  // 날짜 순서 X
                .closeEnrollmentDateTime(LocalDateTime.of(2019, 4, 4, 23, 57))
                .beginEventDateTime(LocalDateTime.of(2019, 4, 3, 23, 57))
                .endEventDateTime(LocalDateTime.of(2019, 4, 2, 23, 57))
                .basePrice(10000)   // min, max 가 맞지 않음
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2 startup factory")
                .build();

        this.mockMvc.perform(post("/api/events")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("content[0].objectName").exists())
                .andExpect(jsonPath("content[0].defaultMessage").exists())    // 메세지
                .andExpect(jsonPath("content[0].code").exists())              // 에러코드
                .andExpect(jsonPath("_links.index").exists())
        ;
    }

    @Test
    @TestDescription("입력값이 비어있는 경우에 에러가 발생하는 테스트")
    public void createEnvent_Bad_Reequest_Empty_Input() throws Exception {
        EventDto eventDto = EventDto.builder().build();

        this.mockMvc.perform(post("/api/events")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @TestDescription("입력받을 수 없는 값을 사용한 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request() throws Exception {
        // Given
        Event event = Event.builder()
                .id(100)
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
                .free(true)
                .offline(false)
                .eventStatus(EventStatus.PUBLISHED)
                .build();

        mockMvc.perform(post("/api/events")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @TestDescription("정상적으로 이벤트를 실행하는 테스트")
    public void createEvent() throws Exception {
        // Given
        EventDto event = EventDto.builder()
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

        mockMvc.perform(post("/api/events")             // perform : 요청
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON_UTF8)       // JSON content을 넘긴다.
                .accept(MediaTypes.HAL_JSON)                        // HAL : Hypertext Application Language / Accept : response 받기 원하는 요청 설정
                .content(objectMapper.writeValueAsString(event)))  //**  objectMapper.writeValueAsString  : JSON 형식으로 변환
                .andDo(print())                                     // ** print() : 응답 확인하고 싶을 때
                .andExpect(status().isCreated())                    // isCreated  : 201 (   = .andExpect(status().is(201))  )
                .andExpect(jsonPath("id").exists())       // ID가 존재하는지 확인
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, "application/hal+json;charset=UTF-8"))
                .andExpect(jsonPath("id").value(Matchers.not(100)))
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("offline").value(true))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.query-events").exists())
                .andExpect(jsonPath("_links.update-event").exists())
                .andDo(document("create-event",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("query-events").description("link to query events"),
                                linkWithRel("profile").description("link to profile"),
                                linkWithRel("update-event").description("link to update an existing")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
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
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        relaxedResponseFields(
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
                                fieldWithPath("eventStatus").description("event status"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.profile.href").description("link to profile"),
                                fieldWithPath("_links.query-events.href").description("link to query events"),
                                fieldWithPath("_links.update-event.href").description("link to update events")
                        )
                ))
        ;
    }

    private String getBearerToken() throws Exception {
        return "bearer " + getAccessToken();
    }

    private String getAccessToken() throws Exception {
        Account account = Account.builder()
                .email(appProperties.getUserUsername())
                .password(appProperties.getUserPassword())
                .roles(Arrays.stream(RoleType.values()).collect(Collectors.toSet()))
                .build();
        this.accountService.saveAccount(account);

        ResultActions perform = mockMvc.perform(post("/oauth/token")
                .with(httpBasic(appProperties.getClientId(), appProperties.getClientSecret())) // basic auth 생성
                .param("username", appProperties.getUserUsername())
                .param("password", appProperties.getUserPassword())
                .param("grant_type", "password"));

        String response = perform.andReturn().getResponse().getContentAsString();
        Jackson2JsonParser parser = new Jackson2JsonParser();
        return parser.parseMap(response).get("access_token").toString();
    }

}