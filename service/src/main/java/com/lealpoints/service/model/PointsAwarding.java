package com.lealpoints.service.model;

public class PointsAwarding {
    private long companyId;
    private String phoneNumber;
    private float saleAmount;
    private String saleKey;

    public long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(long companyId) {
        this.companyId = companyId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phone) {
        this.phoneNumber = phone;
    }

    public float getSaleAmount() {
        return saleAmount;
    }

    public void setSaleAmount(float saleAmount) {
        this.saleAmount = saleAmount;
    }

    public String getSaleKey() {
        return saleKey;
    }

    public void setSaleKey(String saleKey) {
        this.saleKey = saleKey;
    }
}
