package com.volyx.websocketx.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ClientRepository {

    private Map<ChannelId, ClientInfo> clients = new ConcurrentHashMap<>();
    private static ClientRepository instance = null;

    private ClientRepository(){}

    public static synchronized ClientRepository getInstance() {
        if (instance == null) {
            instance = new ClientRepository();
        }
        return instance;
    }

    public void remove(ChannelId id) {
        clients.remove(id);
    }

    public void put(ChannelId id, Channel channel) {
        clients.put(id, new ClientInfo(channel));
    }

    public List<ClientInfo> getClientInfos() {
        return Collections.unmodifiableList(new ArrayList<>(clients.values()));
    }

    public static class ClientInfo {
        private Date startDate = new Date();
        private Channel channel;

        public ClientInfo(Channel channel) {
            this.channel = channel;
        }

        @Override
        public String toString() {
            return "ClientInfo{" +
                    "startDate=" + startDate +
                    ", channel=" + channel +
                    '}';
        }
        public Date getStartDate() {
            return startDate;
        }
        public Channel getChannel() {
            return channel;
        }
    }
}
