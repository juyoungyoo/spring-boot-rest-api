package com.juyoung.restapiwithspring.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.juyoung.restapiwithspring.accounts.Account;
import com.juyoung.restapiwithspring.accounts.AccountRepository;
import com.juyoung.restapiwithspring.accounts.AccountService;
import com.juyoung.restapiwithspring.accounts.RoleType;
import com.juyoung.restapiwithspring.common.TestDescription;
import com.juyoung.restapiwithspring.configs.AppProperties;
import org.codehaus.jackson.JsonParser;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.oauth2.common.util.Jackson2JsonParser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/*
 * @WebMvcTest
 * - 슬라이싱 테스트
 * - Web 관련된 Bean 모두 등록
 */
//@WebMvcTest
@RunWith(SpringRunner.class)
@SpringBootTest         // @SpringBootApplication를 찾아 모든 @bean을 등록한다.
@AutoConfigureMockMvc
@ActiveProfiles("test")
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
    AppProperties appProperties;

    // WebMvcTest 웹용 bean만 등록, repository bean 생성 안해준다.
    // mock 객체 : null
//    @MockBean
//    private EventRepository eventRepository;

    @Before
    public void setUp() throws Exception {
        accountRepository.deleteAll();
    }

    @Test @TestDescription("30개의_이벤트를_10개씩_두번째_페이지_조회하기")
    public void queryEvents() {
    }

    @Test @TestDescription("입력값이 잘못 들어온 경우 에러가 발생하는 테스트")
    public void createEnvent_Bad_Reequest_Wrong_Input() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2019,4,5,23,57))  // 날짜 순서 X
                .closeEnrollmentDateTime(LocalDateTime.of(2019,4,4,23,57))
                .beginEventDateTime(LocalDateTime.of(2019,4,3,23,57))
                .endEventDateTime(LocalDateTime.of(2019,4,2,23,57))
                .basePrice(10000)   // min, max 가 맞지 않음
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2 startup factory")
                .build();

        this.mockMvc.perform(post("/api/events/")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print() )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].objectName").exists())
//                .andExpect(jsonPath("$[0].field").exists())   // field
                .andExpect(jsonPath("$[0].defaultMessage").exists())    // 메세지
                .andExpect(jsonPath("$[0].code").exists())              // 에러코드
//                .andExpect(jsonPath("$[0].rejectedValue").exists())             // 입력을 거절당한 value 값
        ;
    }

    @Test @TestDescription("입력값이 비어있는 경우에 에러가 발생하는 테스트")
    public void createEnvent_Bad_Reequest_Empty_Input() throws Exception {
        EventDto eventDto = EventDto.builder().build();

        this.mockMvc.perform(post("/api/events/")
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
                .beginEnrollmentDateTime(LocalDateTime.of(2019,4,2,23,57))
                .closeEnrollmentDateTime(LocalDateTime.of(2019,4,3,23,57))
                .beginEventDateTime(LocalDateTime.of(2019,4,4,23,57))
                .endEventDateTime(LocalDateTime.of(2019,4,5,23,57))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2 startup factory")
                .free(true)
                .offline(false)
                .eventStatus(EventStatus.PUBLISHED)
                .build();

        mockMvc.perform(post("/api/events/")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event)) )
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
                .beginEnrollmentDateTime(LocalDateTime.of(2019,4,2,23,57))
                .closeEnrollmentDateTime(LocalDateTime.of(2019,4,3,23,57))
                .beginEventDateTime(LocalDateTime.of(2019,4,4,23,57))
                .endEventDateTime(LocalDateTime.of(2019,4,5,23,57))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2 startup factory")
                .build();

        // eventrepository에 save를 호출하면, event를 리턴하라
//        event.setId(10);
//        Mockito.when(eventRepository.save(event)).thenReturn(event);    // eventRepository.save(obj)가 들어갔을 때, mocking 한다. 하지만, 실제로는 다른객체(EventDto)로 repository.save 실행했음으로 해당 mockito가 실행되지 X


        mockMvc.perform(post("/api/events/")             // perform : 요청
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON_UTF8)       // JSON content을 넘긴다.
                .accept(MediaTypes.HAL_JSON)                        // HAL : Hypertext Application Language / Accept : response 받기 원하는 요청 설정
                .content(objectMapper.writeValueAsString(event)) )  //**  objectMapper.writeValueAsString  : JSON 형식으로 변환
                .andDo(print())                                     // ** print() : 응답 확인하고 싶을 때
                .andExpect(status().isCreated())                    // isCreated  : 201 (   = .andExpect(status().is(201))  )
                .andExpect(jsonPath("id").exists())       // ID가 존재하는지 확인
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, "application/hal+json;charset=UTF-8"))
                .andExpect(jsonPath("id").value(Matchers.not(100)))
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("offline").value(true))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
//                .andExpect(jsonPath("_links.self").exists())
//                .andExpect(jsonPath("_link.query-events").exists())
//                .andExpect(jsonPath("_link.update-event").exists())
//                .andExpect(jsonPath("_link.profile").exists())
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
