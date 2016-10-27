package com.volyx.rpc.json;

import com.google.gson.*;
import com.volyx.rpc.common.Request;
import com.volyx.rpc.common.Response;
import com.volyx.rpc.common.TextFrame;

import java.lang.reflect.Type;

import static com.volyx.rpc.common.RequestImpl.REQUEST;

public class TextFrameSerializer implements JsonSerializer<TextFrame>, JsonDeserializer<TextFrame> {
    @Override
    public TextFrame deserialize(JsonElement json, Type type, JsonDeserializationContext ctx) throws JsonParseException {
        final JsonObject jsonObject = json.getAsJsonObject();
        int textFrameType = jsonObject.get("type").getAsInt();
        if (REQUEST == textFrameType) {
            return ctx.deserialize(json, Request.class);
        }
        return ctx.deserialize(json, Response.class);
    }

    @Override
    public JsonElement serialize(TextFrame textFrame, Type type, JsonSerializationContext ctx) {
        final int textFrameType = textFrame.getType();
        if (REQUEST == textFrameType) {
            return ctx.serialize(textFrame, Request.class);
        }
        return ctx.serialize(textFrame, Response.class);
    }
}
