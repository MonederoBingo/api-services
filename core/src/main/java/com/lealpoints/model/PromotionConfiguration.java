package com.lealpoints.model;

import org.json.JSONObject;

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

    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("promotion_configuration_id", promotionConfigurationId);
        jsonObject.put("company_id", companyId);
        jsonObject.put("description", description == null ? "" : description);
        jsonObject.put("required_points", requiredPoints);
        return jsonObject;
    }

    public static PromotionConfiguration fromJSONObject(JSONObject jsonObject) {
        PromotionConfiguration promotionConfiguration = new PromotionConfiguration();
        promotionConfiguration.setPromotionConfigurationId(jsonObject.getLong("promotion_configuration_id"));
        promotionConfiguration.setCompanyId(jsonObject.getLong("company_id"));
        promotionConfiguration.setDescription(jsonObject.getString("description"));
        promotionConfiguration.setRequiredPoints((float) jsonObject.getDouble("required_points"));
        return promotionConfiguration;
    }
}
