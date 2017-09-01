package com.lealpoints.model;

import org.json.JSONObject;

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

    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("points_configuration_id", pointsConfigurationId);
        jsonObject.put("company_id", companyId);
        jsonObject.put("points_to_earn", pointsToEarn);
        jsonObject.put("required_amount", requiredAmount);
        return jsonObject;
    }
}
