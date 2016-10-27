package com.volyx.rpc.json;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.volyx.rpc.common.Result;

import java.lang.reflect.Type;

public class ResultSerializer implements JsonSerializer<Result> {
    @Override
    public JsonElement serialize(Result result, Type type, JsonSerializationContext ctx) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("value", ctx.serialize(result.getValue()));
        return jsonObject;
    }
}
