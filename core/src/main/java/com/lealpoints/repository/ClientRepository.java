package com.lealpoints.repository;


import com.lealpoints.db.util.DbBuilder;
import com.lealpoints.model.Client;
import com.lealpoints.model.CompanyClientMapping;
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

        ServiceResult body = responseEntity.getBody();
        ServiceResult result;
        if (body.isSuccess()) {
            result = new ServiceResult(
                    body.isSuccess(),
                    body.getMessage(),
                    body.getObject());
        } else {
            result = new ServiceResult(false, "Error returning client");
        }
        return result;
    }

    public CompanyClientMapping getByCompanyIdPhone(final long companyId, final String phone) throws Exception {
        return getQueryAgent().selectObject(new DbBuilder<CompanyClientMapping>() {
            @Override
            public String sql() {
                StringBuilder sql = new StringBuilder();
                sql.append("SELECT * FROM client");
                sql.append(" INNER JOIN company_client_mapping USING (client_id)");
                sql.append(" WHERE company_client_mapping.company_id = ?");
                sql.append(" AND client.phone = ? ;");
                return sql.toString();
            }

            @Override
            public Object[] values() {
                return new Object[]{companyId, phone};
            }

            @Override
            public CompanyClientMapping build(ResultSet resultSet) throws SQLException {
                CompanyClientMapping companyClientMapping = new CompanyClientMapping();
                companyClientMapping.setClient(buildClient(resultSet));
                companyClientMapping.setPoints(resultSet.getFloat("points"));
                return companyClientMapping;
            }
        });
    }

    public Client getByPhone(final String phone) throws Exception {
        return getQueryAgent().selectObject(new DbBuilder<Client>() {
            @Override
            public String sql() throws SQLException {
                StringBuilder sql = new StringBuilder();
                sql.append("SELECT * FROM client");
                sql.append(" WHERE phone = ? ;");
                return sql.toString();
            }

            @Override
            public Object[] values() {
                return new Object[]{phone};
            }

            @Override
            public Client build(ResultSet resultSet) throws SQLException {
                Client client = new Client();
                client.setClientId(resultSet.getLong("client_id"));
                client.setPhone(resultSet.getString("phone"));
                return client;
            }
        });
    }

    public long insert(Client client) throws Exception {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO client (phone, can_receive_promo_sms)");
        sql.append(" VALUES (");
        sql.append("'").append(client.getPhone()).append("', ");
        sql.append("").append(client.canReceivePromotionSms()).append(");");
        return getQueryAgent().executeInsert(sql.toString(), "client_id");
    }

    public Client insertIfDoesNotExist(String phone, boolean canReceivePromotionSms) throws Exception {
        Client client = getByPhone(phone);
        if (client == null) {
            client = new Client();
            client.setPhone(phone);
            client.setCanReceivePromotionSms(canReceivePromotionSms);
            client.setClientId(insert(client));
        }
        return client;
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