package com.lealpoints.model;

@Table
public class Client {
    private long clientId;
    private String phone;
    private boolean canReceivePromotionSms;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public long getClientId() {
        return clientId;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    public boolean canReceivePromotionSms() {
        return canReceivePromotionSms;
    }

    public void setCanReceivePromotionSms(boolean canReceivePromotionSms) {
        this.canReceivePromotionSms = canReceivePromotionSms;
    }
}
