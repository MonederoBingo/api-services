package com.lealpoints.repository;


import com.lealpoints.model.Client;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import xyz.greatapp.libs.service.ServiceResult;
import xyz.greatapp.libs.service.context.ThreadContextService;
import xyz.greatapp.libs.service.database.common.ApiClientUtils;
import xyz.greatapp.libs.service.database.requests.SelectQueryRQ;
import xyz.greatapp.libs.service.database.requests.fields.ColumnValue;
import xyz.greatapp.libs.service.database.requests.fields.Join;
import xyz.greatapp.libs.service.location.ServiceLocator;

import java.sql.ResultSet;
import java.sql.SQLException;

import static xyz.greatapp.libs.service.ServiceName.DATABASE;

@Component
public class ClientRepository extends BaseRepository {

    private static final Common c = new Common();
    @Autowired
    private ServiceLocator serviceLocator;
    @Autowired
    private ThreadContextService threadContextService;

    private ApiClientUtils apiClientUtils = new ApiClientUtils();

    public xyz.greatapp.libs.service.ServiceResult getByCompanyId(final long companyId) throws Exception {
        ColumnValue[] filters = new ColumnValue[]{
                new ColumnValue("company_id", companyId)
        };
        Join[] joins = new Join[]{
                new Join("client", "client_id", "client_id")
        };
        HttpEntity<SelectQueryRQ> entity = c.getHttpEntityForSelect(new SelectQueryRQ("company_client_mapping", filters, joins));
        String url = serviceLocator.getServiceURI(DATABASE, threadContextService.getEnvironment()) + "/selectList";
        ResponseEntity<ServiceResult> responseEntity = apiClientUtils.getRestTemplate().postForEntity(
                url,
                entity,
                ServiceResult.class);

        return responseEntity.getBody();
    }

    public xyz.greatapp.libs.service.ServiceResult getByCompanyIdPhone(final long companyId, final String phone) throws Exception {
        ServiceResult result = getByCompanyId(companyId);
        JSONArray clients = new JSONArray(result.getObject());
        for (int i = 0; i < clients.length(); i++) {
            if(phone.equals(clients.getJSONObject(i).get("phone"))) {
                return new ServiceResult(true, "", clients.getJSONObject(i).toString());
            }
        }
        return new ServiceResult(false, "Client not found.");
    }

    public ServiceResult getByPhone(final String phone) throws Exception {
        ColumnValue[] filters = new ColumnValue[]{
                new ColumnValue("phone", phone)
        };
        HttpEntity<SelectQueryRQ> entity = c.getHttpEntityForSelect(new SelectQueryRQ("client", filters, new Join[0]));
        String url = serviceLocator.getServiceURI(DATABASE, threadContextService.getEnvironment()) + "/select";
        ResponseEntity<ServiceResult> responseEntity = apiClientUtils.getRestTemplate().postForEntity(
                url,
                entity,
                ServiceResult.class);
        return responseEntity.getBody();
    }

    public long insert(Client client) throws Exception {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO client (phone, can_receive_promo_sms)");
        sql.append(" VALUES (");
        sql.append("'").append(client.getPhone()).append("', ");
        sql.append("").append(client.canReceivePromotionSms()).append(");");
        return getQueryAgent().executeInsert(sql.toString(), "client_id");
    }

    public ServiceResult insertIfDoesNotExist(String phone, boolean canReceivePromotionSms) throws Exception {
        ServiceResult serviceResult = getByPhone(phone);
        if (serviceResult.getObject().equals("{}")) {
            Client client = new Client();
            client.setPhone(phone);
            client.setCanReceivePromotionSms(canReceivePromotionSms);
            client.setClientId(insert(client));
            serviceResult = new ServiceResult(serviceResult.isSuccess(), serviceResult.getMessage(), new JSONObject(client).toString());
        }
        return serviceResult;
    }

    public int updateCanReceivePromoSms(long clientId, boolean canReceivePromo) throws Exception {
        return getQueryAgent().executeUpdate(
                "UPDATE client SET can_receive_promo_sms = " + canReceivePromo + " WHERE client_id = " + clientId + ";");
    }

    private Client buildClient(ResultSet resultSet) throws SQLException {
        Client client = new Client();
        client.setClientId(resultSet.getLong("client_id"));
        client.setPhone(resultSet.getString("phone"));
        client.setCanReceivePromotionSms(resultSet.getBoolean("can_receive_promo_sms"));
        return client;
    }
}