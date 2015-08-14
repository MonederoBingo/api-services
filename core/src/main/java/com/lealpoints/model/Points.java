package com.lealpoints.model;

import java.util.Date;

@Table
public class Points {
    private long pointsId;
    private long clientId;
    private long companyId;
    private String saleKey;
    private float saleAmount;
    private float pointsToEarn;
    private float requiredAmount;
    private float earnedPoints;
    private Date date;

    public long getPointsId() {
        return pointsId;
    }

    public void setPointsId(long pointsId) {
        this.pointsId = pointsId;
    }

    public long getClientId() {
        return clientId;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    public long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(long companyId) {
        this.companyId = companyId;
    }

    public String getSaleKey() {
        return saleKey;
    }

    public void setSaleKey(String saleKey) {
        this.saleKey = saleKey;
    }

    public float getSaleAmount() {
        return saleAmount;
    }

    public void setSaleAmount(float saleAmount) {
        this.saleAmount = saleAmount;
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

    public float getEarnedPoints() {
        return earnedPoints;
    }

    public void setEarnedPoints(float earnedPoints) {
        this.earnedPoints = earnedPoints;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
