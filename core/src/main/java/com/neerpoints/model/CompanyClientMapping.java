package com.neerpoints.model;

@Table
public class CompanyClientMapping {
    private long companyClientMappingId;
    private long companyId;
    private long clientId;
    private float points;

    public long getCompanyClientMappingId() {
        return companyClientMappingId;
    }

    public void setCompanyClientMappingId(long companyClientMappingId) {
        this.companyClientMappingId = companyClientMappingId;
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

    public float getPoints() {
        return points;
    }

    public void setPoints(float points) {
        this.points = points;
    }
}
