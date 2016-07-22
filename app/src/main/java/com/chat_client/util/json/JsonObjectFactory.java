package com.chat_client.util.json;

import com.chat_client.util.entity.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public final class JsonObjectFactory {
    private JsonObjectFactory() {}

    private static final ObjectMapper mapper = new ObjectMapper();

    public static String getJsonString(String command, User user) throws JsonProcessingException {
        JsonObject jsonObject = new JsonObject(command, user);
        return mapper.writeValueAsString(jsonObject);
    }

    public static <T> T getObjectFromJson(String json, Class<T> tClass) throws IOException {
        try {
            return mapper.readValue(json, tClass);
        } catch (IOException e) {
            return null;
        }
    }
}
