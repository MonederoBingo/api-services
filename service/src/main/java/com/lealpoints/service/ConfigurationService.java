package com.lealpoints.service;

import java.util.Map;

public interface ConfigurationService extends BaseService {

    public void loadConfiguration();

    public void reloadConfiguration();

    public Map<String, String> getConfigurationMap();

    public String getConfiguration(String key);

    public String getUncachedConfiguration(String key);
}
