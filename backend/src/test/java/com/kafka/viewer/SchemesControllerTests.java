package com.kafka.viewer;

import com.kafka.viewer.entity.response.BundledSchemaResponse;
import com.kafka.viewer.entity.response.S3SchemaResponse;
import org.junit.Test;
import org.springframework.context.annotation.Profile;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Profile("s3")
public class SchemesControllerTests extends BaseTest {
    @Test
    public void statusTest() throws Exception {
        mockMvc
                .perform(get("/api/v1/schemes/status"))
                .andExpect(status().isOk());
    }


    @Test
    public void bundledSchemesTest() throws Exception {
        MvcResult mvcResult = mockMvc
                .perform(get("/api/v1/schemes/bundled"))
                .andExpect(status().isOk())
                .andReturn();

        BundledSchemaResponse bundledSchemas =
                unmarshalResponse(mvcResult, BundledSchemaResponse.class);

        assertTrue(bundledSchemas.getBundledSchemas().size() > 0);
    }

    @Test
    public void s3SchemesTest() throws Exception {
        MvcResult mvcResult = mockMvc
                .perform(get("/api/v1/schemes/s3"))
                .andExpect(status().isOk())
                .andReturn();

        S3SchemaResponse bundledSchemas =
                unmarshalResponse(mvcResult, S3SchemaResponse.class);


        // TODO migrate to AssertJ
        // TODO Use kafka mocks and mockito (learn kafka testing)
        assertTrue(bundledSchemas.getS3Schemas().size() > 0);
    }
}
