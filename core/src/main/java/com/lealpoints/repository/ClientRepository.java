package com.lealpoints.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import com.lealpoints.db.util.DbBuilder;
import com.lealpoints.model.Client;
import com.lealpoints.model.CompanyClientMapping;
import org.springframework.stereotype.Component;

@Component
public class ClientRepository extends BaseRepository {

    public List<CompanyClientMapping> getByCompanyId(final long companyId) throws Exception {
        return getQueryAgent().selectList(new DbBuilder<CompanyClientMapping>() {
            @Override
            public String sql() {
                StringBuilder sql = new StringBuilder();
                sql.append("SELECT * FROM client");
                sql.append(" INNER JOIN company_client_mapping USING (client_id)");
                sql.append(" WHERE company_client_mapping.company_id = ").append("?").append(" ;");
                setValue(companyId);
                return sql.toString();
            }
            private Object[] value=new Object[1];
            @Override
            public Object[] getValue() {
                return value;
            }

            @Override
            public void setValue(Object valueT) {
                value[0]=valueT;
            }

            @Override
            public CompanyClientMapping build(ResultSet resultSet) throws SQLException {
                CompanyClientMapping companyClientMapping = new CompanyClientMapping();
                companyClientMapping.setCompanyClientMappingId(resultSet.getLong("company_client_mapping_id"));
                companyClientMapping.setCompanyId(resultSet.getLong("company_id"));
                companyClientMapping.setPoints(resultSet.getFloat("points"));
                Client client = buildClient(resultSet);
                companyClientMapping.setClient(client);
                return companyClientMapping;
            }
        });
    }

    public CompanyClientMapping getByCompanyIdPhone(final long companyId, final String phone) throws Exception {
        return getQueryAgent().selectObject(new DbBuilder<CompanyClientMapping>() {
            @Override
            public String sql() {
                StringBuilder sql = new StringBuilder();
                sql.append("SELECT * FROM client");
                sql.append(" INNER JOIN company_client_mapping USING (client_id)");
                sql.append(" WHERE company_client_mapping.company_id = ?");
                sql.append(" AND client.phone = ").append("?").append(" ;");

                setValue(companyId);
                setValue(phone);
                return sql.toString();
            }

            private Object[] value = new Object[2];
            private int index=0;
            @Override
            public Object[] getValue() {
                return value;
            }

            @Override
            public void setValue(Object valueT) {
                this.value[index]=valueT;
                index++;
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
                sql.append(" WHERE phone = ").append("?").append(";");
                setValue(phone);
                return sql.toString();
            }
            private Object[] value=new Object[1];
            @Override
            public void setValue(Object valueT) {
                this.value[0]=valueT;
            }

            @Override
            public Object[] getValue() {
              return value;
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
