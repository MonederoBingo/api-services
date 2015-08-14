package com.lealpoints.model;

@Table
public class PointsConfiguration {

    private long pointsConfigurationId;
    private long companyId;
    private float pointsToEarn;
    private float requiredAmount;

    public long getPointsConfigurationId() {
        return pointsConfigurationId;
    }

    public void setPointsConfigurationId(long pointsConfigurationId) {
        this.pointsConfigurationId = pointsConfigurationId;
    }

    public long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(long companyId) {
        this.companyId = companyId;
    }

    public float getPointsToEarn() {
        return pointsToEarn;
    }

    public void setPointsToEarn(float pointsToEarn) {
        this.pointsToEarn = pointsToEarn;
    }

    public float getRequiredAmount() {
        return requiredAmount;
    }

    public void setRequiredAmount(float requiredAmount) {
        this.requiredAmount = requiredAmount;
    }
}
