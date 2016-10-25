package com.volyx.websocketx.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.volyx.websocketx.common.Request;

import java.lang.reflect.Type;

public class RequestSerializer implements JsonSerializer<Request> {
    @Override
    public JsonElement serialize(Request request, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();

        result.addProperty("method", request.getMethod());
        result.addProperty("params", request.getParams());

        return result;
    }
}
