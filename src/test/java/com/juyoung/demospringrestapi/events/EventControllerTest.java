package com.juyoung.demospringrestapi.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


// todo : 10. Event 생성 API 구현 : 201 응답받기
/*
 * @WebMvcTest
 * - 슬라이싱 테스트
 * - Web 관련된 Bean 모두 등록
 */
//@WebMvcTest
@RunWith(SpringRunner.class)
@SpringBootTest         // @SpringBootApplication를 찾아 모든 @bean을 등록한다.
@AutoConfigureMockMvc
public class EventControllerTest {

    /* @mockMvc :  웹 서버를 띄우지 않고 Spring MVC ( DispatherServlet )가 처리하는 과정 확인이 가능하여 '컨트롤러 테스트'로 많이 사용한다. */
    @Autowired
    private MockMvc mockMvc;    // mocking : 가짜 요청/응답 확인가능 ( 속도 : 웹 구동 < mockMvc < 단위 테스트 )

    @Autowired
    private ObjectMapper objectMapper;

    // WebMvcTest 웹용 bean만 등록, repository bean 생성 안해준다.
    // mock 객체 : null
//    @MockBean
//    private EventRepository eventRepository;

    @Test
    public void createEvent() throws Exception {
        // Given
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

        // eventrepository에 save를 호출하면, event를 리턴하라
//        event.setId(10);
//        Mockito.when(eventRepository.save(event)).thenReturn(event);    // eventRepository.save(obj)가 들어갔을 때, mocking 한다. 하지만, 실제로는 다른객체(EventDto)로 repository.save 실행했음으로 해당 mockito가 실행되지 X


        mockMvc.perform(post("/api/events/")             // perform : 요청
                .contentType(MediaType.APPLICATION_JSON_UTF8)       // JSON content을 넘긴다.
                .accept(MediaTypes.HAL_JSON)                        // HAL : Hypertext Application Language / Accept : response 받기 원하는 요청 설정
                .content(objectMapper.writeValueAsString(event)) )  //**  objectMapper.writeValueAsString  : JSON 형식으로 변환
                .andDo(print())                                     // ** print() : 응답 확인하고 싶을 때
                .andExpect(status().isCreated())                    // isCreated  : 201 (   = .andExpect(status().is(201))  )
                .andExpect(jsonPath("id").exists())       // ID가 존재하는지 확인
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, "application/hal+json;charset=UTF-8"))
                .andExpect(jsonPath("id").value(Matchers.not(100)))
                .andExpect(jsonPath("free").value(Matchers.not(true)))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
        ;
    }


}
