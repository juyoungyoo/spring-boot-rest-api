package com.juyoung.restapiwithspring.index;

import com.juyoung.restapiwithspring.common.BaseControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class IndexControllerTest extends BaseControllerTest {

    @WithMockUser
    @Test
    void index() throws Exception {
        this.mockMvc.perform(get("/api"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("_links.events").exists());
    }
}
