package com.lealpoints.repository;

import com.lealpoints.model.ClientUser;
import org.json.JSONObject;
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

import static xyz.greatapp.libs.service.ServiceName.DATABASE;

@Component
public class ClientUserRepository extends BaseRepository {

    private static final Common c = new Common();
    private final ClientRepository clientRepository;
    private ServiceLocator serviceLocator;
    private ThreadContextService threadContextService;
    private ApiClientUtils apiClientUtils = new ApiClientUtils();

    @Autowired
    public ClientUserRepository(ServiceLocator serviceLocator, ThreadContextService threadContextService, ClientRepository clientRepository) {
        this.serviceLocator = serviceLocator;
        this.threadContextService = threadContextService;
        this.clientRepository = clientRepository;
    }

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

        ServiceResult client = clientRepository.getByPhone(phone);
        ColumnValue[] values = new ColumnValue[]{
                new ColumnValue("sms_key", smsKey)
        };
        ColumnValue[] filter = new ColumnValue[]{
                new ColumnValue("client_id", new JSONObject(client.getObject()).getLong("client_id"))
        };
        HttpEntity<UpdateQueryRQ> entity = c.getHttpEntityForUpdate(new UpdateQueryRQ("client_user", values, filter));
        String url = serviceLocator.getServiceURI(DATABASE, threadContextService.getEnvironment()) + "/update";
        ResponseEntity<ServiceResult> responseEntity = apiClientUtils.getRestTemplate().postForEntity(
                url,
                entity,
                ServiceResult.class);
        return Integer.parseInt(responseEntity.getBody().getObject());

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

    public ServiceResult getByClientUserIdApiKey(final Integer userId, final String apiKey) throws Exception {
        ColumnValue[] filters = new ColumnValue[]{
                new ColumnValue("client_user_id", userId),
                new ColumnValue("api_key", apiKey)
        };
        HttpEntity<SelectQueryRQ> entity = c.getHttpEntityForSelect(new SelectQueryRQ("client_user", filters, new Join[0]));
        String url = serviceLocator.getServiceURI(DATABASE, threadContextService.getEnvironment()) + "/select";
        ResponseEntity<ServiceResult> responseEntity = apiClientUtils.getRestTemplate().postForEntity(
                url,
                entity,
                ServiceResult.class);
        return responseEntity.getBody();
    }
}
