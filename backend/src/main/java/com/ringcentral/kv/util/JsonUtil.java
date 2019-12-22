package com.ringcentral.kv.util;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;

@Component
public class JsonUtil {

    @Autowired
    private ObjectMapper objectMapper;

    public JsonUtil() {}

    public JsonUtil(ObjectMapper objectMapper) {

        this.objectMapper = objectMapper;
    }


    public <T> T unmarshalResponse(String json, Class<T> clazz, ObjectMapper objectMapper) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public <T> T unmarshalResponse(String json, Class<T> clazz) {

        return unmarshalResponse(json, clazz, objectMapper);

    }

    public <T> String marshalRequestObject(T object, ObjectMapper objectMapper) {

        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public <T> String marshalRequestObject(T object) {

        return marshalRequestObject(object, objectMapper);
    }

    /**
     * Useful for debugging ONLY, but be warned that this method
     * will create a new and default {@link ObjectMapper} if one
     * doesn't already exist.
     */
    public <T> void printObjectToString(T object) {

        if (objectMapper == null) {
            ObjectMapper mapper = new ObjectMapper();
            System.out.println(marshalRequestObject(object, mapper));
        } else {
            System.out.println(marshalRequestObject(object));
        }
    }

    public HashMap<String, String> parseJsonToHashmap(String dataForJsonParser, String mappingExclusion) throws Exception  {

        HashMap<String, String> fieldValuePairs = new HashMap<>();

        JsonFactory factory = new JsonFactory();
        JsonParser parser = factory.createParser(dataForJsonParser);

        String field = null;
        String value = null;

        while(!parser.isClosed()) {
            JsonToken jsonToken = parser.nextToken();


            if((parser.getValueAsString() != null) && (jsonToken != null)) {
                if (jsonToken.equals(JsonToken.FIELD_NAME) && (!parser.getValueAsString().equals(mappingExclusion))) {
                    field = parser.getValueAsString();
                } else if (jsonToken.equals(JsonToken.VALUE_STRING)) {
                    value = parser.getValueAsString();
                }

                if((field != null) && (value != null)) {
                    if(field.equals("uii")) {
                        value = "'" + value;
                    }
                    fieldValuePairs.put(field, value);
                    field = null;
                    value = null;
                }
            }

        }

        parser.close();
        return fieldValuePairs;
    }


    /**
     * Injests a Json String and returns a HashMap<String, String> of key/value pairs from the JSON
     *
     * @param json
     *
     * @return
     * @throws IOException
     */
    public static HashMap<String, String> parseJsonToHashMapBasic(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<HashMap<String, String>> typeRef = new TypeReference<HashMap<String, String>>() {};
        return mapper.readValue(json, typeRef);
    }
}
