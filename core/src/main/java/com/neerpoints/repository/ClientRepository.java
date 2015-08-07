package com.neerpoints.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import com.neerpoints.db.DbBuilder;
import com.neerpoints.model.Client;
import com.neerpoints.model.CompanyClientMapping;
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
                sql.append(" WHERE company_client_mapping.company_id = ").append(companyId).append(";");
                return sql.toString();
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
                sql.append(" WHERE company_client_mapping.company_id = ").append(companyId);
                sql.append(" AND client.phone = '").append(phone).append("';");
                return sql.toString();
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
            public String sql() {
                StringBuilder sql = new StringBuilder();
                sql.append("SELECT * FROM client");
                sql.append(" WHERE phone = '").append(phone).append("';");
                return sql.toString();
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
