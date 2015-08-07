package com.neerpoints.model;

@Table
public class CompanyClientMapping {
    private long companyClientMappingId;
    private long companyId;
    private Client client;
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

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public float getPoints() {
        return points;
    }

    public void setPoints(float points) {
        this.points = points;
    }
}
