package com.lealpoints.model;

import org.json.JSONObject;

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

    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("company_client_mapping_id", companyClientMappingId);
        jsonObject.put("company_id", companyId);
        if(client == null) {
            jsonObject.put("client_id", 0);
        } else {
            jsonObject.put("client_id", client.getClientId());
        }
        jsonObject.put("points", points);
        return jsonObject;
    }

    public static CompanyClientMapping fromJSONObject(JSONObject jsonObject) {
        CompanyClientMapping companyClientMapping = new CompanyClientMapping();
        companyClientMapping.setCompanyClientMappingId(jsonObject.getLong("company_client_mapping_id"));
        companyClientMapping.setCompanyId(jsonObject.getLong("company_id"));
        Client client = new Client();
        client.setClientId(jsonObject.getLong("client_id"));
        companyClientMapping.setClient(client);
        companyClientMapping.setPoints((float) jsonObject.optDouble("points", 0));
        return companyClientMapping;
    }
}
