package com.juyoung.restapiwithspring.events;

import com.juyoung.restapiwithspring.accounts.AccountRepository;
import com.juyoung.restapiwithspring.common.BaseControllerTest;
import com.juyoung.restapiwithspring.error.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

import java.time.LocalDateTime;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
class EventControllerTest2 extends BaseControllerTest {

    private static String EVENT_URL = "/api/events";
    private static String EVENT_URL_WITH_ID = "/api/events/{id}";

    private EventCreateUpdateDto fixtureOfEvent;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private EventRepository eventRepository;

    @BeforeEach
    void setUp() {
        fixtureOfEvent = EventCreateUpdateDto.builder()
                .name("Spring seminal")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2019, 9, 20, 0, 0))
                .closeEnrollmentDateTime(LocalDateTime.of(2019, 9, 30, 0, 0))
                .beginEventDateTime(LocalDateTime.of(2019, 10, 1, 23, 57))
                .endEventDateTime(LocalDateTime.of(2019, 10, 3, 23, 57))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2 startup factory")
                .build();

        eventRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @DisplayName("없는 이벤트를 조회 시 실패")
    @Test
    void readEvent_whenNoExistEvent_thenException() throws Exception {
        int noExistEventId = 9999;

        mockMvc.perform(get(EVENT_URL_WITH_ID, noExistEventId))
                .andExpect(status().isNotFound())
                .andDo(document("get-an-event-bad-request",
                        getCustomErrorResponseSnippet()
                ));
    }

    @DisplayName("이벤트 상세정보 보기 성공")
    @Test
    void readEvent_success() throws Exception {
        Event event = generateEvent();

        getResource(EVENT_URL_WITH_ID, event.getId())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("id").exists())
                .andDo(document("get-an-event",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("query-events").description("link to query events"),
                                linkWithRel("profile").description("link to profile"),
                                linkWithRel("update-account-event")
                                        .description("link to update account an existing").optional()
                        ),
                        getResponseFieldsSnippet()
                ));
    }

    @DisplayName("입력값이 잘못된 경우 이벤트 생성 실패")
    @Test
    void createEvent_whenWrongInput_thenBadRequest() throws Exception {
        EventCreateUpdateDto eventDto = EventCreateUpdateDto.builder()
                .name(null)
                .build();

        postResource(EVENT_URL, eventDto)
                .andExpect(status().isBadRequest())
                .andDo(document("create-event-bad-request",
                        getErrorResponseSnippet()
                ));
    }

    @DisplayName("입력값이 비어있는 경우 이벤트 생성 실패")
    @Test
    void createEvent_whenEmptyInput_thenBadRequest() throws Exception {
        EventCreateUpdateDto eventDto = EventCreateUpdateDto.builder().build();

        postResource(EVENT_URL, eventDto)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(ErrorCode.INPUT_VALUE_INVALID.getCode()))
                .andExpect(jsonPath("message").exists())
                .andDo(document("create-event-bad-request-empty",
                        getErrorResponseSnippet()
                ));
    }

    @DisplayName("이벤트 등록 성공")
    @Test
    void createEvent_success() throws Exception {
        EventCreateUpdateDto eventDto = fixtureOfEvent;

        postResource(EVENT_URL, eventDto)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("offline").value(true))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
                .andDo(document("create-event",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("query-events").description("link to query events"),
                                linkWithRel("profile").description("link to profile"),
                                linkWithRel("update-account-event").description("link to updateAccount an existing")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        getRequestFieldsSnippet(),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        getResponseFieldsSnippet()
                ));
    }

    private Event generateEvent() throws Exception {
        String response = postResource(EVENT_URL, fixtureOfEvent)
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readValue(response, Event.class);
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

    private ResponseFieldsSnippet getErrorResponseSnippet() {
        return responseFields(
                fieldWithPath("code").description("error code"),
                fieldWithPath("message").description("error message"),
                fieldWithPath("errors[].field").description("Invalid field name").optional(),
                fieldWithPath("errors[].value").description("error custom description").optional(),
                fieldWithPath("errors[].reason").description("error details").optional()
        );
    }

    private ResponseFieldsSnippet getCustomErrorResponseSnippet() {
        return responseFields(
                fieldWithPath("code").description("error code"),
                fieldWithPath("message").description("error message"),
                fieldWithPath("errors").description("error details")
        );
    }
}
