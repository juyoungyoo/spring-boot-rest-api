package com.juyoung.restapiwithspring.common;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@Ignore
public class BaseControllerTest {

    /* @mockMvc :  웹 서버를 띄우지 않고 Spring MVC ( DispatherServlet )가 처리하는 과정 확인이 가능하여 '컨트롤러 테스트'로 많이 사용한다. */
    @Autowired
    protected MockMvc mockMvc;    // mocking : 가짜 요청/응답 확인가능 ( 속도 : 웹 구동 < mockMvc < 단위 테스트 )
    @Autowired
    protected ObjectMapper objectMapper;  // spring boot 자동 매핑 ( bean )
    @Autowired
    protected ModelMapper modelMapper;

}
