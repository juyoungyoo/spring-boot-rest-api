package com.juyoung.restapiwithspring.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.juyoung.restapiwithspring.accounts.Account;
import com.juyoung.restapiwithspring.accounts.AccountRepository;
import com.juyoung.restapiwithspring.accounts.AccountService;
import com.juyoung.restapiwithspring.accounts.RoleType;
import com.juyoung.restapiwithspring.common.RestDocsConfiguration;
import com.juyoung.restapiwithspring.common.TestDescription;
import com.juyoung.restapiwithspring.configs.AppProperties;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.common.util.Jackson2JsonParser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
public class EventControllerTest {

    /* @mockMvc :  웹 서버를 띄우지 않고 Spring MVC ( DispatherServlet )가 처리하는 과정 확인이 가능하여 '컨트롤러 테스트'로 많이 사용한다. */
    @Autowired
    private MockMvc mockMvc;    // mocking : 가짜 요청/응답 확인가능 ( 속도 : 웹 구동 < mockMvc < 단위 테스트 )

    @Autowired
    private ObjectMapper objectMapper;  // spring boot 자동 매핑 ( bean )

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
    }

    @Test
    @TestDescription("30개의_이벤트를_10개씩_두번째_페이지_조회하기")
    public void queryEvents() throws Exception {
        // Given
        IntStream.range(0, 30).forEach(this::generateEvent);

        // When
        mockMvc.perform(get("/api/events")
                .param("page", "1")
                .param("size", "10")
                .param("sort", "name,DESC"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("query-events"))
                ;
    }

    private void generateEvent(int i) {
        Event event = Event.builder()
                .name("event " + i)
                .description("test event")
                .build();
        eventRepository.save(event);
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
        // Givenr
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
                        responseFields(
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
