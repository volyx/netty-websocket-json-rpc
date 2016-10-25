package com.volyx.websocketx.json;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.volyx.websocketx.repository.ClientRepository;

import java.lang.reflect.Type;

public class ClientInfoSerializer implements JsonSerializer<ClientRepository.ClientInfo>{
    @Override
    public JsonElement serialize(ClientRepository.ClientInfo clientInfo, Type type, JsonSerializationContext ctx) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("startDate", ctx.serialize(clientInfo.getStartDate()));
        jsonObject.addProperty("channel", clientInfo.getChannel().toString());
        return jsonObject;
    }
}
