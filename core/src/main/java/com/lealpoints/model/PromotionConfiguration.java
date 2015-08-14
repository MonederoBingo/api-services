package com.lealpoints.model;

@Table
public class PromotionConfiguration {
    private long promotionConfigurationId;
    private long companyId;
    private String description;
    private float requiredPoints;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getRequiredPoints() {
        return requiredPoints;
    }

    public void setRequiredPoints(float requiredPoints) {
        this.requiredPoints = requiredPoints;
    }
}
