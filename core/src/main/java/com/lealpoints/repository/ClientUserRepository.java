package com.lealpoints.repository;

import com.lealpoints.db.util.DbBuilder;
import com.lealpoints.model.ClientUser;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ClientUserRepository extends BaseRepository {

    public long insert(ClientUser clientUser) throws Exception {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO client_user (client_id, name, email, password, sms_key)");
        sql.append(" VALUES (");
        sql.append(clientUser.getClientId()).append(", ");
        sql.append("'").append(clientUser.getName()).append("', ");
        sql.append(clientUser.getEmail() == null ? null : "'" + clientUser.getEmail() + "'").append(", ");
        sql.append("'").append(clientUser.getPassword()).append("', ");
        sql.append("'").append(clientUser.getSmsKey()).append("');");

        return getQueryAgent().executeInsert(sql.toString(), "client_user_id");
    }

    public int updateSmsKey(String smsKey, String phone) throws Exception {
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE client_user");
        sql.append(" SET sms_key = '").append(smsKey).append("'");
        sql.append(" WHERE client_id = (SELECT client_id FROM client WHERE phone = '").append(phone).append("');");
        return getQueryAgent().executeUpdate(sql.toString());
    }

    public ClientUser getByClientId(final long clientId) throws Exception {
        return getQueryAgent().selectObject(new DbBuilder<ClientUser>() {
            @Override
            public String sql() {
                StringBuilder sql = new StringBuilder();
                sql.append("SELECT client_user.* FROM ").append("client_user");
                sql.append(" WHERE client_id = ?;");
                return sql.toString();
            }

            @Override
            public Object[] values() {
                return new Object[]{clientId};
            }

            @Override
            public ClientUser build(ResultSet resultSet) throws SQLException {
                return buildClientUser(resultSet);
            }
        });
    }

    public ClientUser getByPhoneAndKey(final String phone, final String smsKey) throws Exception {
        return getQueryAgent().selectObject(new DbBuilder<ClientUser>() {
            @Override
            public String sql() {
                StringBuilder sql = new StringBuilder();
                sql.append("SELECT client_user.* FROM ").append("client_user");
                sql.append(" INNER JOIN client USING (client_id)");
                sql.append(" WHERE client.phone = ? ");
                sql.append(" AND client_user.sms_key = ?;");
                return sql.toString();
            }

            @Override
            public Object[] values() {
                return new Object[]{phone, smsKey};
            }

            @Override
            public ClientUser build(ResultSet resultSet) throws SQLException {
                return buildClientUser(resultSet);
            }
        });
    }

    public ClientUser getByEmailAndPassword(final String email, final String password) throws Exception {
        return getQueryAgent().selectObject(new DbBuilder<ClientUser>() {
            @Override
            public String sql() {
                StringBuilder sql = new StringBuilder();
                sql.append("SELECT client_user.* FROM ").append("client_user");
                sql.append(" WHERE client_user.email = ?");
                sql.append(" AND client_user.password = ?");
                return sql.toString();
            }

            @Override
            public Object[] values() {
                return new Object[]{email, password};
            }

            @Override
            public ClientUser build(ResultSet resultSet) throws SQLException {
                return buildClientUser(resultSet);
            }
        });
    }

    public int updateApiKeyById(long clientUserId, String apiKey) throws Exception {
        return getQueryAgent()
            .executeUpdate("UPDATE client_user SET api_key = '" + apiKey + "' WHERE client_user_id = '" + clientUserId + "';");
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
