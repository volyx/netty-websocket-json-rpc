package com.volyx.rpc.json;

import com.google.gson.*;
import com.volyx.rpc.common.BatchRequest;
import com.volyx.rpc.common.Request;
import com.volyx.rpc.common.RequestImpl;

import java.lang.reflect.Type;
import java.util.UUID;

public class BatchRequestSerializer implements JsonSerializer<BatchRequest>, JsonDeserializer<BatchRequest> {
    @Override
    public BatchRequest deserialize(JsonElement json, Type type, JsonDeserializationContext ctx) throws JsonParseException {
        final JsonArray jsonArray = json.getAsJsonArray();
        final BatchRequest batchRequest = new BatchRequest(UUID.randomUUID().toString());
        for (JsonElement jsonElement : jsonArray) {
            batchRequest.add(ctx.deserialize(jsonElement, RequestImpl.class));
        }
        return batchRequest;
    }

    @Override
    public JsonElement serialize(BatchRequest batch, Type type, JsonSerializationContext ctx) {
        JsonArray jsonArray = new JsonArray();
        for (Request request : batch.getRequests()) {
            jsonArray.add(ctx.serialize(request, RequestImpl.class));
        }
        return jsonArray;
    }
}
