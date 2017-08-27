package com.lealpoints.model;

import org.json.JSONObject;

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

    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("client_id", clientId);
        jsonObject.put("phone", phone);
        jsonObject.put("can_receive_promo_sms", canReceivePromotionSms);
        return jsonObject;
    }
}
