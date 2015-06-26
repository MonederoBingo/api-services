package com.neerpoints.service.model;

public class ClientLoginResult {
    private long clientUserId;
    private String apiKey;

    public long getClientUserId() {
        return clientUserId;
    }

    public void setClientUserId(long clientUserId) {
        this.clientUserId = clientUserId;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
