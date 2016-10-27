package com.volyx.rpc.json;

import com.google.gson.*;
import com.volyx.rpc.common.RequestImpl;

import java.lang.reflect.Type;
import java.util.UUID;

public class RequestImplSerializer implements JsonSerializer<RequestImpl>, JsonDeserializer<RequestImpl> {
    @Override
    public JsonElement serialize(RequestImpl request, Type type, JsonSerializationContext jsonSerializationContext) {
        final JsonObject result = new JsonObject();
        result.addProperty("method", request.getMethod());
        result.addProperty("params", request.getParams());
        result.addProperty("id", request.getId());
        result.addProperty("type", request.getType());
        return result;
    }

    @Override
    public RequestImpl deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext ctx) throws JsonParseException {
        final JsonObject jsonObject = jsonElement.getAsJsonObject();
        final String method = jsonObject.get("method").getAsString();
        final String params = jsonObject.get("params").getAsString();
        final String id;
        if (jsonObject.has("id")) {
         id = jsonObject.get("id").getAsString();
        } else {
            id = UUID.randomUUID().toString();
        }
        return new RequestImpl(id, method, params);
    }
}
