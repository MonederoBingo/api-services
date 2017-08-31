package com.lealpoints.model;

import org.json.JSONObject;

@Table
public class CompanyUser {

    private long companyUserId;
    private long companyId;
    private String name;
    private String email;
    private String password;
    private boolean active;
    private String activationKey;
    private String language;
    private Boolean mustChangePassword;
    private String apiKey;

    public long getCompanyUserId() {
        return companyUserId;
    }

    public void setCompanyUserId(long companyUserId) {
        this.companyUserId = companyUserId;
    }

    public long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(long companyId) {
        this.companyId = companyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getActivationKey() {
        return activationKey;
    }

    public void setActivationKey(String activationKey) {
        this.activationKey = activationKey;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Boolean getMustChangePassword() {
        return mustChangePassword == null ? false : mustChangePassword;
    }

    public void setMustChangePassword(Boolean mustChangePassword) {
        this.mustChangePassword = mustChangePassword;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("company_user_id", companyUserId);
        jsonObject.put("company_id", companyId);
        jsonObject.put("name", name);
        jsonObject.put("email", email);
        jsonObject.put("password", password);
        jsonObject.put("active", active);
        jsonObject.put("activation_key", activationKey == null ? "" : activationKey);
        jsonObject.put("language", language);
        jsonObject.put("must_change_password", mustChangePassword);
        jsonObject.put("api_key", apiKey);
        return jsonObject;
    }
}
