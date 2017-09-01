package com.lealpoints.model;

import org.json.JSONObject;

/**
 * Created by aayala on 9/1/15.
 */
@Table
public class Configuration {
    private long configurationId;
    private String name;
    private String description;
    private String value;

    public long getConfigurationId() {
        return configurationId;
    }

    public void setConfigurationId(long configurationId) {
        this.configurationId = configurationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("configuration_id", configurationId);
        jsonObject.put("name", name);
        jsonObject.put("description", description);
        jsonObject.put("value", value);
        return jsonObject;
    }
}
