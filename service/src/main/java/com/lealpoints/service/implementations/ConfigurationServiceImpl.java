package com.lealpoints.service.implementations;

import com.lealpoints.repository.ConfigurationRepository;
import com.lealpoints.service.ConfigurationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.greatapp.libs.service.ServiceResult;

import java.util.HashMap;
import java.util.Map;

@Component
public class ConfigurationServiceImpl extends BaseServiceImpl implements ConfigurationService {
    private final Logger logger = LogManager.getLogger(ConfigurationServiceImpl.class.getName());
    private final Map<String, String> _configurationMap = new HashMap<>();
    private final ConfigurationRepository _configurationRepository;

    @Autowired
    private ConfigurationServiceImpl(ConfigurationRepository configurationRepository) {
        super(null);
        _configurationRepository = configurationRepository;
    }

    public void loadConfiguration() {
        if (_configurationMap.isEmpty()) {
            reloadConfiguration();
        }
    }

    public void reloadConfiguration() {
        _configurationMap.clear();
        try {
            final ServiceResult configurationList = _configurationRepository.getConfigurationList();
            JSONArray jsonArray = new JSONArray(configurationList.getObject());
            for (int i = 0; i < jsonArray.length(); i++) {
                _configurationMap.put(jsonArray.getJSONObject(i).getString("name"), jsonArray.getJSONObject(i).getString("value"));
            }
        } catch (Exception e) {
            logger.error("Could not load configuration list", e);
            throw new RuntimeException(e);
        }
    }

    public Map<String, String> getConfigurationMap() {
        if (_configurationMap.isEmpty()) {
            loadConfiguration();
        }
        return _configurationMap;
    }

    public String getConfiguration(String key) {
        return getConfigurationMap().get(key);
    }

    public String getUncachedConfiguration(String key) {
        try {
            return _configurationRepository.getValueByName(key).getObject();
        } catch (Exception e) {
            logger.error("Could not load configuration: " + key, e);
            throw new RuntimeException(e);
        }
    }
}
