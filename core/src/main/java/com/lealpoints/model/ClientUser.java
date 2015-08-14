package com.lealpoints.model;

@Table
public class ClientUser {
    private long clientUserId;
    private long clientId;
    private String name;
    private String email;
    private String password;
    private String smsKey;
    private String apiKey;

    public long getClientUserId() {
        return clientUserId;
    }

    public void setClientUserId(long clientUserId) {
        this.clientUserId = clientUserId;
    }

    public long getClientId() {
        return clientId;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSmsKey() {
        return smsKey;
    }

    public void setSmsKey(String smsKey) {
        this.smsKey = smsKey;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
