package com.lealpoints.service.model;

public class PromotionApplying {
    private long promotionConfigurationId;
    private long companyId;
    private String phoneNumber;

    public long getPromotionConfigurationId() {
        return promotionConfigurationId;
    }

    public void setPromotionConfigurationId(long promotionConfigurationId) {
        this.promotionConfigurationId = promotionConfigurationId;
    }

    public long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(long companyId) {
        this.companyId = companyId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
