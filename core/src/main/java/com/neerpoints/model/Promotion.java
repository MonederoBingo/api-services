package com.neerpoints.model;

import java.util.Date;

@Table
public class Promotion {
    private long promotionId;
    private long companyId;
    private long clientId;
    private String description;
    private float usedPoints;
    private Date date;

    public long getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(long promotionId) {
        this.promotionId = promotionId;
    }

    public long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(long companyId) {
        this.companyId = companyId;
    }

    public long getClientId() {
        return clientId;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getUsedPoints() {
        return usedPoints;
    }

    public void setUsedPoints(float usedPoints) {
        this.usedPoints = usedPoints;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
