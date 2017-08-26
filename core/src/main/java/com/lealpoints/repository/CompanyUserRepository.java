package com.lealpoints.repository;

import com.lealpoints.DatabaseServiceResult;
import com.lealpoints.UpdateQuery;
import com.lealpoints.context.ThreadContextService;
import com.lealpoints.db.util.DbBuilder;
import com.lealpoints.model.CompanyUser;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import org.json.JSONArray;
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
import xyz.greatapp.libs.service.requests.database.ColumnValue;
import xyz.greatapp.libs.service.requests.database.InsertQueryRQ;
import xyz.greatapp.libs.service.requests.database.SelectQueryRQ;

import java.sql.ResultSet;
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

    public List<CompanyUser> getByCompanyId(final long companyId) throws Exception {

        ColumnValue[] filters = new ColumnValue[]{
                new ColumnValue("company_id", companyId)
        };

        HttpEntity<SelectQueryRQ> entity = new HttpEntity<>(
                new SelectQueryRQ("company_user", filters),
                getHttpHeaders());
        ResponseEntity<DatabaseServiceResult> responseEntity = getRestTemplate().postForEntity(
                getDatabaseURL() + "/selectList",
                entity,
                DatabaseServiceResult.class);
        if (responseEntity.getBody().getObject().equals("{}")) {
            return null;
        }

        JSONArray jsonArray = new JSONArray(responseEntity.getBody().getObject().toString());
        List<CompanyUser> companyUserList = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            CompanyUser companyUser = new CompanyUser();
            companyUser.setCompanyId(jsonObject.getLong("company_id"));
            companyUser.setCompanyUserId(jsonObject.getLong("company_user_id"));
            companyUser.setActive(jsonObject.getBoolean("active"));
            companyUser.setEmail(jsonObject.getString("email"));
            companyUser.setName(jsonObject.getString("name"));
            companyUserList.add(companyUser);
        }

        return companyUserList;
    }

    public CompanyUser getByEmailAndPassword(final String email, final String password) throws Exception {
        ColumnValue[] filters = new ColumnValue[]{
                new ColumnValue("email", email),
                new ColumnValue("password", password)
        };

        HttpEntity<SelectQueryRQ> entity = new HttpEntity<>(
                new SelectQueryRQ("company_user", filters),
                getHttpHeaders());
        ResponseEntity<DatabaseServiceResult> responseEntity = getRestTemplate().postForEntity(
                getDatabaseURL() + "/select",
                entity,
                DatabaseServiceResult.class);
        if (responseEntity.getBody().getObject().equals("{}")) {
            return null;
        }
        return buildCompanyUser(new JSONObject(responseEntity.getBody()).getString("object"));
    }

    public CompanyUser getByEmail(final String email) throws Exception {

        SelectQueryRQ selectQueryRQ = new SelectQueryRQ("company_user", new ColumnValue[]{
                new ColumnValue("email", email)
        });
        HttpEntity<SelectQueryRQ> entity = new HttpEntity<>(
                selectQueryRQ,
                getHttpHeaders());


        ResponseEntity<DatabaseServiceResult> responseEntity = getRestTemplate().postForEntity(
                getDatabaseURL() + "/select",
                entity,
                DatabaseServiceResult.class);
        if (responseEntity.getBody().getObject().equals("{}")) {
            return null;
        }
        return buildCompanyUser(new JSONObject(responseEntity.getBody()).getString("object"));
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
        UpdateQuery updateQuery = new UpdateQuery("UPDATE company_user SET active = true WHERE activation_key = '" + activationKey + "';");
        ResponseEntity<DatabaseServiceResult> responseEntity = restTemplate.postForEntity(
                getDatabaseURL() + "/update",
                updateQuery,
                DatabaseServiceResult.class);
        return parseInt(responseEntity.getBody().getObject().toString());
    }

    public int updatePasswordByEmail(final String email, final String password, final boolean mustChangePassword) throws Exception {
        return getQueryAgent().executeUpdate(
                "UPDATE company_user SET password = '" + password + "', must_change_password = " + mustChangePassword +
                        " WHERE email = '" + email + "';");
    }

    public int updateApiKeyByEmail(final String email, final String apiKey) throws Exception {
        RestTemplate restTemplate = getRestTemplate();
        UpdateQuery updateQuery = new UpdateQuery("UPDATE company_user SET api_key = '" + apiKey + "' WHERE email = '" + email + "';");
        ResponseEntity<DatabaseServiceResult> responseEntity = restTemplate.postForEntity(
                getDatabaseURL() + "/update",
                updateQuery,
                DatabaseServiceResult.class);
        return parseInt(responseEntity.getBody().getObject().toString());
    }

    public int clearActivationKey(final String activationKey) throws Exception {
        return getQueryAgent().executeUpdate("UPDATE company_user SET activation_key = NULL WHERE activation_key = '" + activationKey + "';");
    }

    public CompanyUser getByCompanyUserIdApiKey(final Integer companyUserId, final String apiKey) throws Exception {
        return getQueryAgent().selectObject(new DbBuilder<CompanyUser>() {
            @Override
            public String sql() {
                StringBuilder sql = new StringBuilder();
                sql.append("SELECT company_user.* FROM company_user");
                sql.append(" WHERE company_user.api_key = ?").append(";");
                return sql.toString();
            }

            @Override
            public Object[] values() {
                return new Object[]{apiKey};
            }

            @Override
            public CompanyUser build(ResultSet resultSet) throws SQLException {
                return buildCompanyUser(resultSet);
            }
        });
    }

    private CompanyUser buildCompanyUser(ResultSet resultSet) throws SQLException {
        CompanyUser companyUser = new CompanyUser();
        companyUser.setCompanyUserId(resultSet.getLong("company_user_id"));
        companyUser.setCompanyId(resultSet.getLong("company_id"));
        companyUser.setName(resultSet.getString("name"));
        companyUser.setEmail(resultSet.getString("email"));
        companyUser.setPassword(resultSet.getString("password"));
        companyUser.setActive(resultSet.getBoolean("active"));
        companyUser.setActivationKey(resultSet.getString("activation_key"));
        companyUser.setLanguage(resultSet.getString("language"));
        companyUser.setMustChangePassword(resultSet.getBoolean("must_change_password"));
        companyUser.setApiKey(resultSet.getString("api_key"));
        return companyUser;
    }

    private CompanyUser buildCompanyUser(String stringObject) throws SQLException {
        JSONObject object = new JSONObject(stringObject);
        CompanyUser companyUser = new CompanyUser();
        companyUser.setCompanyUserId(object.getLong("company_user_id"));
        companyUser.setCompanyId(object.getLong("company_id"));
        companyUser.setName(object.getString("name"));
        companyUser.setEmail(object.getString("email"));
        companyUser.setPassword(object.getString("password"));
        companyUser.setActive(object.getBoolean("active"));
        companyUser.setActivationKey(object.getString("activation_key"));
        companyUser.setLanguage(object.getString("language"));
        companyUser.setMustChangePassword(object.getBoolean("must_change_password"));
        companyUser.setApiKey(object.getString("api_key"));
        return companyUser;
    }
}
