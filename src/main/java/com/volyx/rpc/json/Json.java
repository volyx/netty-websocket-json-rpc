package com.volyx.rpc.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.volyx.rpc.common.*;
import com.volyx.rpc.repository.ClientRepository;

public class Json {
    private static Gson gson;

    public static Gson getInstance() {
        if (gson == null) {
            gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .registerTypeAdapter(TextFrame.class, new TextFrameSerializer())
                    .registerTypeAdapter(Request.class, new RequestSerializer())
                    .registerTypeAdapter(RequestImpl.class, new RequestImplSerializer())
                    .registerTypeAdapter(BatchRequest.class, new BatchRequestSerializer())
                    .registerTypeAdapter(Response.class, new ResponseSerializer())
                    .registerTypeAdapter(Result.class, new ResultSerializer())
                    .registerTypeAdapter(ClientRepository.ClientInfo.class, new ClientInfoSerializer())
                    .create();
        }
        return gson;
    }

}
