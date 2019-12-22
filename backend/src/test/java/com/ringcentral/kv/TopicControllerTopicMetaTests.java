package com.ringcentral.kv;

import com.ringcentral.kv.entity.TopicsInfo;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TopicControllerTopicMetaTests extends BaseTest {

    @Test
    public void testTopicInfoMetadataCount() throws Exception {
        MvcResult topicsInfoResults = mockMvc
                .perform(get("/api/v1/topics/info"))
                .andExpect(status().isOk())
                .andReturn();

        TopicsInfo topicsInfo = unmarshalResponse(topicsInfoResults, TopicsInfo.class);

        Set<String> topicNames = topicsInfo.getNames();

        topicNames.forEach(topicName -> {
            try {
                mockMvc
                        .perform(get("/api/v1/topics/{topicName}/info", topicName))
                        .andExpect(status().isOk());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    public void testTopicMessages() throws Exception {
        final Integer offset = 0;
        final Integer count = 10;
        final Integer partition = 0;

        MvcResult topicsInfoResults = mockMvc
                .perform(get("/api/v1/topics/info"))
                .andExpect(status().isOk())
                .andReturn();

        TopicsInfo topicsInfo = unmarshalResponse(topicsInfoResults, TopicsInfo.class);

        Set<String> topicNames = topicsInfo.getNames();

        topicNames.forEach(topicName -> {
            try {
                mockMvc
                        .perform(
                                get("/api/v1/topics/{topicName}/messages/partition/{partition}/offset/{offset}/count/{count}",
                                        topicName, partition, offset, count))
                        .andExpect(status().isOk());

                // TODO unmarshal and check response
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
