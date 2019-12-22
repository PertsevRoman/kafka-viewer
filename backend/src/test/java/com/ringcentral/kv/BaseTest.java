package com.ringcentral.kv;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ringcentral.kv.util.JsonUtil;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public abstract class BaseTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected JsonUtil jsonUtil;

    @Autowired
    protected ObjectMapper objectMapper;

    public <T> T unmarshalResponse(MvcResult result, TypeReference<?> ref) {
        try {

            int httpStatus = result.getResponse().getStatus();
            if ((httpStatus == HttpStatus.OK.value()) || (httpStatus == HttpStatus.ACCEPTED.value()) || (httpStatus == HttpStatus.CREATED.value())) {
                return objectMapper.readValue(result.getResponse().getContentAsString(), ref);
            } else {
                throw new Exception("Integration test failed with http response error code: " + httpStatus);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public <T> String marshalRequestObject(T object) {
        return jsonUtil.marshalRequestObject(object);
    }

    public <T> T unmarshalResponse(MvcResult result, Class<T> clazz) {

        try {
            return jsonUtil.unmarshalResponse(result.getResponse().getContentAsString(), clazz);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
