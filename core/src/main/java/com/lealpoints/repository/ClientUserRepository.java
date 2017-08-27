package com.lealpoints.repository;

import com.lealpoints.db.util.DbBuilder;
import com.lealpoints.model.ClientUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import xyz.greatapp.libs.service.ServiceResult;
import xyz.greatapp.libs.service.context.ThreadContextService;
import xyz.greatapp.libs.service.database.common.ApiClientUtils;
import xyz.greatapp.libs.service.database.requests.InsertQueryRQ;
import xyz.greatapp.libs.service.database.requests.SelectQueryRQ;
import xyz.greatapp.libs.service.database.requests.UpdateQueryRQ;
import xyz.greatapp.libs.service.database.requests.fields.ColumnValue;
import xyz.greatapp.libs.service.database.requests.fields.Join;
import xyz.greatapp.libs.service.location.ServiceLocator;

import java.sql.ResultSet;
import java.sql.SQLException;

import static xyz.greatapp.libs.service.ServiceName.DATABASE;

@Component
public class ClientUserRepository extends BaseRepository {

    private static final Common c = new Common();
    @Autowired
    private ServiceLocator serviceLocator;
    @Autowired
    private ThreadContextService threadContextService;
    private ApiClientUtils apiClientUtils = new ApiClientUtils();

    public long insert(ClientUser clientUser) throws Exception {
        ColumnValue[] values = new ColumnValue[]{
                new ColumnValue("client_id", clientUser.getClientId()),
                new ColumnValue("name", clientUser.getName()),
                new ColumnValue("password", clientUser.getEmail()),
                new ColumnValue("sms_key", clientUser.getSmsKey())
        };
        HttpEntity<InsertQueryRQ> entity = c.getHttpEntityForInsert(new InsertQueryRQ("client_user", values, "client_user_id"));
        String url = serviceLocator.getServiceURI(DATABASE, threadContextService.getEnvironment()) + "/insert";
        ResponseEntity<ServiceResult> responseEntity = apiClientUtils.getRestTemplate().postForEntity(
                url,
                entity,
                ServiceResult.class);
        return Long.parseLong(responseEntity.getBody().getObject());
    }

    public int updateSmsKey(String smsKey, String phone) throws Exception {
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE client_user");
        sql.append(" SET sms_key = '").append(smsKey).append("'");
        sql.append(" WHERE client_id = (SELECT client_id FROM client WHERE phone = '").append(phone).append("');");
        return getQueryAgent().executeUpdate(sql.toString());
    }

    public xyz.greatapp.libs.service.ServiceResult getByClientId(final long clientId) throws Exception {
        ColumnValue[] filters = new ColumnValue[]{
                new ColumnValue("client_id", clientId)
        };
        HttpEntity<SelectQueryRQ> entity = c.getHttpEntityForSelect(new SelectQueryRQ("client_user", filters, new Join[0]));
        String url = serviceLocator.getServiceURI(DATABASE, threadContextService.getEnvironment()) + "/select";
        ResponseEntity<ServiceResult> responseEntity = apiClientUtils.getRestTemplate().postForEntity(
                url,
                entity,
                ServiceResult.class);

        return responseEntity.getBody();
    }

    public xyz.greatapp.libs.service.ServiceResult getByPhoneAndKey(final String phone, final String smsKey) throws Exception {
        Join[] joins = new Join[]{
                new Join("client", "client_id", "client_id")
        };
        ColumnValue[] filters = new ColumnValue[]{
                new ColumnValue("phone", phone, "client"),
                new ColumnValue("sms_key", smsKey)
        };
        HttpEntity<SelectQueryRQ> entity = c.getHttpEntityForSelect(new SelectQueryRQ("client_user", filters, joins));
        String url = serviceLocator.getServiceURI(DATABASE, threadContextService.getEnvironment()) + "/select";
        ResponseEntity<ServiceResult> responseEntity = apiClientUtils.getRestTemplate().postForEntity(
                url,
                entity,
                ServiceResult.class);

        return responseEntity.getBody();
    }

    public xyz.greatapp.libs.service.ServiceResult getByEmailAndPassword(final String email, final String password) throws Exception {
        ColumnValue[] filters = new ColumnValue[]{
                new ColumnValue("email", email),
                new ColumnValue("password", password)
        };
        HttpEntity<SelectQueryRQ> entity = c.getHttpEntityForSelect(new SelectQueryRQ("client_user", filters, new Join[0]));
        String url = serviceLocator.getServiceURI(DATABASE, threadContextService.getEnvironment()) + "/select";
        ResponseEntity<ServiceResult> responseEntity = apiClientUtils.getRestTemplate().postForEntity(
                url,
                entity,
                ServiceResult.class);

        return responseEntity.getBody();
    }

    public int updateApiKeyById(long clientUserId, String apiKey) throws Exception {
        getQueryAgent()
                .executeUpdate("UPDATE client_user SET api_key = '" + apiKey + "' WHERE client_user_id = '" + clientUserId + "';");

        ColumnValue[] values = new ColumnValue[]{
                new ColumnValue("api_key", apiKey)
        };
        ColumnValue[] filter = new ColumnValue[]{
                new ColumnValue("client_user_id", clientUserId)
        };
        HttpEntity<UpdateQueryRQ> entity = c.getHttpEntityForUpdate(new UpdateQueryRQ("client_user", values, filter));
        String url = serviceLocator.getServiceURI(DATABASE, threadContextService.getEnvironment()) + "/update";
        ResponseEntity<ServiceResult> responseEntity = apiClientUtils.getRestTemplate().postForEntity(
                url,
                entity,
                ServiceResult.class);
        return Integer.parseInt(responseEntity.getBody().getObject());
    }

    public ClientUser getByClientUserIdApiKey(final Integer userId, final String apiKey) throws Exception {
        return getQueryAgent().selectObject(new DbBuilder<ClientUser>() {
            @Override
            public String sql() {
                StringBuilder sql = new StringBuilder();
                sql.append("SELECT client_user.* FROM client_user");
                sql.append(" WHERE client_user.client_user_id = ").append("?").append("");
                sql.append(" AND client_user.api_key = ?;");
                return sql.toString();
            }

            @Override
            public Object[] values() {
                return new Object[]{userId, apiKey};
            }

            @Override
            public ClientUser build(ResultSet resultSet) throws SQLException {
                return buildClientUser(resultSet);
            }
        });
    }

    private ClientUser buildClientUser(ResultSet resultSet) throws SQLException {
        ClientUser clientUser = new ClientUser();
        clientUser.setClientUserId(resultSet.getLong("client_user_id"));
        clientUser.setClientId(resultSet.getLong("client_id"));
        clientUser.setName(resultSet.getString("name"));
        clientUser.setEmail(resultSet.getString("email"));
        clientUser.setPassword(resultSet.getString("password"));
        clientUser.setSmsKey(resultSet.getString("sms_key"));
        return clientUser;
    }
}
