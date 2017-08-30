package com.lealpoints.model;

import org.json.JSONObject;

@Table
public class Company {
    private long companyId;
    private String name;
    private String urlImageLogo;

    public long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(long companyId) {
        this.companyId = companyId;
    }

    public String getName() {
        return name != null ? name : "";
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrlImageLogo() {
        return urlImageLogo;
    }

    public void setUrlImageLogo(String urlImageLogo) {
        this.urlImageLogo = urlImageLogo;
    }

    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("company_id", companyId);
        jsonObject.put("name", name == null ? "" : name);
        jsonObject.put("url_image_logo", urlImageLogo);
        return jsonObject;
    }
}
