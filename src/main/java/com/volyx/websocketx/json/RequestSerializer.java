package com.volyx.websocketx.json;

import com.google.gson.*;
import com.volyx.websocketx.common.Request;
import org.apache.logging.log4j.core.util.UuidUtil;

import java.lang.reflect.Type;
import java.util.UUID;

public class RequestSerializer implements JsonSerializer<Request>, JsonDeserializer<Request> {
    @Override
    public JsonElement serialize(Request request, Type type, JsonSerializationContext jsonSerializationContext) {
        final JsonObject result = new JsonObject();
        result.addProperty("method", request.getMethod());
        result.addProperty("params", request.getParams());
        result.addProperty("id", request.getId());
        return result;
    }

    @Override
    public Request deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

        final JsonObject jsonObject = jsonElement.getAsJsonObject();
        final String method = jsonObject.get("method").getAsString();
        final String params = jsonObject.get("params").getAsString();
        final String id = UUID.randomUUID().toString();
        return new Request(id, method, params);
    }
}
