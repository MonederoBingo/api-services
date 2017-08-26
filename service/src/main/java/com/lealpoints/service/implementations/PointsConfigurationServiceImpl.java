package com.lealpoints.service.implementations;

import static java.util.Collections.emptyMap;

import com.google.gson.Gson;
import com.lealpoints.common.PropertyManager;
import com.lealpoints.context.ThreadContextService;
import com.lealpoints.i18n.Language;
import com.lealpoints.model.PointsConfiguration;
import com.lealpoints.service.PointsConfigurationService;
import com.lealpoints.service.response.ServiceMessage;
import com.lealpoints.service.response.ServiceResult;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class PointsConfigurationServiceImpl implements PointsConfigurationService
{
    private final EurekaClient eurekaClient;
    private final ThreadContextService threadContextService;

    @Autowired
    public PointsConfigurationServiceImpl(EurekaClient eurekaClient, ThreadContextService threadContextService) {
        this.eurekaClient = eurekaClient;
        this.threadContextService = threadContextService;
    }

    public ServiceResult<PointsConfiguration> getByCompanyId(long companyId)
    {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response =
                restTemplate.getForEntity(getPointsConfigurationURL() + "/" + companyId, String.class);
        return parseServiceResult(response.getBody(), PointsConfiguration.class);
    }

    private String getPointsConfigurationURL()
    {
        InstanceInfo instanceInfo = eurekaClient.getNextServerFromEureka("points_configuration", false);
        String homePageUrl = instanceInfo.getHomePageUrl();
        boolean hasHttps = homePageUrl.contains("https://");
        homePageUrl = homePageUrl.replace("http://", "");
        homePageUrl = homePageUrl.replace("https://", "");
        homePageUrl = threadContextService.getEnvironment().getURIPrefix() + homePageUrl;
        return hasHttps ? "https://" + homePageUrl : "http://" + homePageUrl;
    }

    private <T> ServiceResult<T> parseServiceResult(String json, Class<T> classOfT)
    {
        try
        {
            JSONObject jsonObject = new JSONObject(json);
            boolean success = jsonObject.getBoolean("success");
            ServiceMessage serviceMessage = parseServiceMessage(jsonObject);
            T object = null;
            if (jsonObject.has("object") && jsonObject.get("object") instanceof JSONObject)
            {
                object = new Gson().fromJson(jsonObject.getJSONObject("object").toString(), classOfT);
            }
            String extraInfo = jsonObject.has("extraInfo") ? jsonObject.getString("extraInfo") : "";
            if (object != null)
            {
                return new ServiceResult<>(success, serviceMessage, object, extraInfo);
            }
            else
            {
                return new ServiceResult<>(success, serviceMessage, classOfT.newInstance(), extraInfo);
            }
        }
        catch (Exception ex)
        {
            throw new RuntimeException("Error when parsing JSON to Service Result. JSON String: " + json, ex);
        }

    }

    private static ServiceMessage parseServiceMessage(JSONObject jsonObject)
    {
        if (!(jsonObject.get("message") instanceof JSONObject))
        {
            return new ServiceMessage("");
        }
        ServiceMessage serviceMessage = new ServiceMessage(jsonObject.getString("message"));
        JSONObject translations = jsonObject.getJSONObject("translations");
        for (String s : translations.keySet())
        {
            serviceMessage.addTranslation(Language.valueOf(s), translations.getString(s));
        }
        return serviceMessage;
    }

    public ServiceResult<Boolean> update(PointsConfiguration pointsConfiguration)
    {
        RestTemplate restTemplate = new RestTemplate();
        String url = PropertyManager.getProperty("points_configuration_url");
        restTemplate.put(url, pointsConfiguration, emptyMap());
        return new ServiceResult<>(true, new ServiceMessage(""), true);
    }

    @Override
    public void registerPointsConfiguration(long companyId) throws Exception {
        PointsConfiguration pointsConfiguration = new PointsConfiguration();
        pointsConfiguration.setCompanyId(companyId);
        pointsConfiguration.setPointsToEarn(1);
        pointsConfiguration.setRequiredAmount(1);

        RestTemplate restTemplate = new RestTemplate();
        String url = PropertyManager.getProperty("points_configuration_url") + companyId;
        ResponseEntity<String> response = restTemplate.postForEntity(url, pointsConfiguration, String.class);
        System.out.println(response);
    }
}
