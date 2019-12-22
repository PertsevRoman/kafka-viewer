package com.ringcentral.kv;


import org.junit.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TopicControllerInfoTests extends BaseTest {

    @Test
    public void topicsInfo() throws Exception {
        mockMvc
            .perform(get("/api/v1/topics/info"))
            .andExpect(status().isOk());
    }

    @Test
    public void topics405() throws Exception {
        mockMvc
            .perform(put("/api/v1/topics/info"))
            .andExpect(status().isMethodNotAllowed());

        mockMvc
            .perform(delete("/api/v1/topics/info"))
            .andExpect(status().isMethodNotAllowed());

        mockMvc
            .perform(post("/api/v1/topics/info"))
            .andExpect(status().isMethodNotAllowed());
    }
}
