package com.lealpoints.repository;

import com.lealpoints.DatabaseServiceResult;
import com.lealpoints.context.ThreadContextService;
import com.lealpoints.model.CompanyUser;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import xyz.greatapp.libs.service.ServiceResult;
import xyz.greatapp.libs.service.database.requests.InsertQueryRQ;
import xyz.greatapp.libs.service.database.requests.SelectQueryRQ;
import xyz.greatapp.libs.service.database.requests.UpdateQueryRQ;
import xyz.greatapp.libs.service.database.requests.fields.ColumnValue;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;

@Component
public class CompanyUserRepository extends BaseRepository {

    private final EurekaClient eurekaClient;
    private final ThreadContextService threadContextService;

    @Autowired
    public CompanyUserRepository(@Qualifier("eurekaClient") EurekaClient eurekaClient, ThreadContextService threadContextService) {
        this.eurekaClient = eurekaClient;
        this.threadContextService = threadContextService;
    }

    public long insert(CompanyUser companyUser) throws Exception {
        ColumnValue[] values = new ColumnValue[]{
                new ColumnValue("company_id", companyUser.getCompanyId()),
                new ColumnValue("name", companyUser.getName()),
                new ColumnValue("email", companyUser.getEmail()),
                new ColumnValue("password", companyUser.getPassword()),
                new ColumnValue("active", companyUser.isActive()),
                new ColumnValue("activation_key", companyUser.getActivationKey()),
                new ColumnValue("language", companyUser.getLanguage()),
                new ColumnValue("must_change_password", companyUser.getMustChangePassword())
        };
        HttpEntity<InsertQueryRQ> entity = new HttpEntity<>(
                new InsertQueryRQ("company_user", values, "company_user_id"),
                getHttpHeaders());
        ResponseEntity<DatabaseServiceResult> responseEntity = getRestTemplate().postForEntity(
                getDatabaseURL() + "/insert",
                entity,
                DatabaseServiceResult.class);
        if (responseEntity.getBody().getObject().equals("{}")) {
            return 0L;
        }
        return Long.parseLong(responseEntity.getBody().getObject().toString());
    }

    private String getDatabaseURL() {
        InstanceInfo instanceInfo = eurekaClient.getNextServerFromEureka("database", false);
        String homePageUrl = instanceInfo.getHomePageUrl();
        boolean hasHttps = homePageUrl.contains("https://");
        homePageUrl = homePageUrl.replace("http://", "");
        homePageUrl = homePageUrl.replace("https://", "");
        homePageUrl = threadContextService.getEnvironment().getURIPrefix() + homePageUrl;
        return hasHttps ? "https://" + homePageUrl : "http://" + homePageUrl;
    }

    public ServiceResult getByCompanyId(final long companyId) throws Exception {

        ColumnValue[] filters = new ColumnValue[]{
                new ColumnValue("company_id", companyId)
        };

        HttpEntity<SelectQueryRQ> entity = new HttpEntity<>(
                new SelectQueryRQ("company_user", filters),
                getHttpHeaders());
        ResponseEntity<ServiceResult> responseEntity = getRestTemplate().postForEntity(
                getDatabaseURL() + "/selectList",
                entity,
                ServiceResult.class);
        if (responseEntity.getBody().getObject().equals("{}")) {
            return null;
        }

        return responseEntity.getBody();
    }

    public ServiceResult getByEmailAndPassword(final String email, final String password) throws Exception {
        ColumnValue[] filters = new ColumnValue[]{
                new ColumnValue("email", email),
                new ColumnValue("password", password)
        };

        HttpEntity<SelectQueryRQ> entity = new HttpEntity<>(
                new SelectQueryRQ("company_user", filters),
                getHttpHeaders());
        ResponseEntity<ServiceResult> responseEntity = getRestTemplate().postForEntity(
                getDatabaseURL() + "/select",
                entity,
                ServiceResult.class);
        if (responseEntity.getBody().getObject().equals("{}")) {
            return null;
        }
        return responseEntity.getBody();
    }

    public ServiceResult getByEmail(final String email) throws Exception {

        SelectQueryRQ selectQueryRQ = new SelectQueryRQ("company_user", new ColumnValue[]{
                new ColumnValue("email", email)
        });
        HttpEntity<SelectQueryRQ> entity = new HttpEntity<>(
                selectQueryRQ,
                getHttpHeaders());


        ResponseEntity<ServiceResult> responseEntity = getRestTemplate().postForEntity(
                getDatabaseURL() + "/select",
                entity,
                ServiceResult.class);
        if (responseEntity.getBody().getObject().equals("{}")) {
            return null;
        }
        return responseEntity.getBody();
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private RestTemplate getRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        List<HttpMessageConverter<?>> list = new ArrayList<>();
        list.add(new MappingJackson2HttpMessageConverter());
        restTemplate.setMessageConverters(list);
        return restTemplate;
    }

    public int updateActivateByActivationKey(final String activationKey) throws Exception {
        RestTemplate restTemplate = getRestTemplate();
        ColumnValue[] sets = new ColumnValue[] {
                new ColumnValue("active", true)
        };
        ColumnValue[] filters = new ColumnValue[] {
                new ColumnValue("activation_key", activationKey)
        };
        UpdateQueryRQ updateQuery = new UpdateQueryRQ("company_user", sets, filters);

        ResponseEntity<DatabaseServiceResult> responseEntity = restTemplate.postForEntity(
                getDatabaseURL() + "/update",
                updateQuery,
                DatabaseServiceResult.class);
        return parseInt(responseEntity.getBody().getObject().toString());
    }

    public int updatePasswordByEmail(final String email, final String password, final boolean mustChangePassword) throws Exception {
        RestTemplate restTemplate = getRestTemplate();
        ColumnValue[] sets = new ColumnValue[] {
                new ColumnValue("password", password),
                new ColumnValue("must_change_password", mustChangePassword)
        };
        ColumnValue[] filters = new ColumnValue[] {
                new ColumnValue("email", email)
        };
        UpdateQueryRQ updateQuery = new UpdateQueryRQ("company_user", sets, filters);
        ResponseEntity<DatabaseServiceResult> responseEntity = restTemplate.postForEntity(
                getDatabaseURL() + "/update",
                updateQuery,
                DatabaseServiceResult.class);
        return parseInt(responseEntity.getBody().getObject().toString());
    }

    public int updateApiKeyByEmail(final String email, final String apiKey) throws Exception {
        RestTemplate restTemplate = getRestTemplate();
        ColumnValue[] sets = new ColumnValue[] {
                new ColumnValue("api_key", apiKey)
        };
        ColumnValue[] filters = new ColumnValue[] {
                new ColumnValue("email", email)
        };
        UpdateQueryRQ updateQuery = new UpdateQueryRQ("company_user", sets, filters);


        ResponseEntity<DatabaseServiceResult> responseEntity = restTemplate.postForEntity(
                getDatabaseURL() + "/update",
                updateQuery,
                DatabaseServiceResult.class);
        return parseInt(responseEntity.getBody().getObject().toString());
    }

    public int clearActivationKey(final String activationKey) throws Exception {
        return getQueryAgent().executeUpdate("UPDATE company_user SET activation_key = NULL WHERE activation_key = '" + activationKey + "';");
    }

    public ServiceResult getByCompanyUserIdApiKey(final Integer companyUserId, final String apiKey) throws Exception {
        SelectQueryRQ selectQueryRQ = new SelectQueryRQ("company_user", new ColumnValue[]{
                new ColumnValue("api_key", apiKey)
        });
        HttpEntity<SelectQueryRQ> entity = new HttpEntity<>(
                selectQueryRQ,
                getHttpHeaders());


        ResponseEntity<ServiceResult> responseEntity = getRestTemplate().postForEntity(
                getDatabaseURL() + "/select",
                entity,
                ServiceResult.class);
        if (responseEntity.getBody().getObject().equals("{}")) {
            return null;
        }
        return responseEntity.getBody();
    }
}
