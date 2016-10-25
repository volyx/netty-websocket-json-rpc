package com.volyx.websocketx.server;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.volyx.websocketx.common.Handler;

import java.lang.reflect.Type;

public class HandlerSerializer implements JsonSerializer<Handler>{
    @Override
    public JsonElement serialize(Handler handler, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", handler.getName());

        return jsonObject;
    }
}
