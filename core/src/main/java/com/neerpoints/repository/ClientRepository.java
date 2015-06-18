package com.neerpoints.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import com.neerpoints.db.DbBuilder;
import com.neerpoints.model.Client;
import com.neerpoints.model.ClientPoints;
import org.springframework.stereotype.Component;

@Component
public class ClientRepository extends BaseRepository {

    public List<ClientPoints> getByCompanyId(final long companyId) throws Exception {
        return getQueryAgent().selectList(new DbBuilder<ClientPoints>() {
            @Override
            public String sql() {
                StringBuilder sql = new StringBuilder();
                sql.append("SELECT client.phone, company_client_mapping.points FROM client");
                sql.append(" INNER JOIN company_client_mapping USING (client_id)");
                sql.append(" WHERE company_client_mapping.company_id = ").append(companyId).append(";");
                return sql.toString();
            }

            @Override
            public ClientPoints build(ResultSet resultSet) throws SQLException {
                ClientPoints clientPoints = new ClientPoints();
                clientPoints.setPhone(resultSet.getString("phone"));
                clientPoints.setPoints(resultSet.getFloat("points"));
                return clientPoints;
            }
        });
    }

    public ClientPoints getByCompanyIdPhone(final long companyId, final String phone) throws Exception {
        return getQueryAgent().selectObject(new DbBuilder<ClientPoints>() {
            @Override
            public String sql() {
                StringBuilder sql = new StringBuilder();
                sql.append("SELECT client.phone, company_client_mapping.points FROM client");
                sql.append(" INNER JOIN company_client_mapping USING (client_id)");
                sql.append(" WHERE company_client_mapping.company_id = ").append(companyId);
                sql.append(" AND client.phone = '").append(phone).append("';");
                return sql.toString();
            }

            @Override
            public ClientPoints build(ResultSet resultSet) throws SQLException {
                ClientPoints clientPoints = new ClientPoints();
                clientPoints.setPhone(resultSet.getString("phone"));
                clientPoints.setPoints(resultSet.getFloat("points"));
                return clientPoints;
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
        sql.append("INSERT INTO client (phone)");
        sql.append(" VALUES (");
        sql.append("'").append(client.getPhone()).append("');");

        return getQueryAgent().executeInsert(sql.toString(), "client_id");
    }

    public Client insertIfDoesNotExist(String phone) throws Exception {
        Client client = getByPhone(phone);
        if (client == null) {
            client = new Client();
            client.setPhone(phone);
            client.setClientId(insert(client));
        }
        return client;
    }
}
