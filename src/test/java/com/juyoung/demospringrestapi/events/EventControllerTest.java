package com.juyoung.demospringrestapi.events;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/*
 * @WebMvcTest
 * - 슬라이싱 테스트
 * - Web 관련된 Bean 모두 등록
 */
@WebMvcTest
@RunWith(SpringRunner.class)
public class EventControllerTest {

    /* @mockMvc :  웹 서버를 띄우지 않고 Spring MVC ( DispatherServlet )가 처리하는 과정 확인이 가능하여 '컨트롤러 테스트'로 많이 사용한다. */
    @Autowired
    private MockMvc mockMvc;    // mocking : 가짜 요청/응답 확인가능 ( 속도 : 웹 구동 < mockMvc < 단위 테스트 )

    @Test
    public void createEvent() throws Exception {
        mockMvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON_UTF8)   // JSON content을 넘긴다.
                .accept(MediaTypes.HAL_JSON)                    // HAL : Hypertext Application Language / Accept : response 받기 원하는 요청 설정
                )   // perform : 요청
                .andExpect(status().isCreated());    // isCreated  : 201 (   = .andExpect(status().is(201))  )

    }
}
