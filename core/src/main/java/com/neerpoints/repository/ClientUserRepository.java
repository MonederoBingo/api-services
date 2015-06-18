package com.neerpoints.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import com.neerpoints.db.DbBuilder;
import com.neerpoints.model.ClientUser;
import org.springframework.stereotype.Component;

@Component
public class ClientUserRepository extends BaseRepository {

    public long insert(ClientUser clientUser) throws Exception {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO client_user (client_id, name, email, password, sms_key)");
        sql.append(" VALUES (");
        sql.append(clientUser.getClientId()).append(", ");
        sql.append("'").append(clientUser.getName()).append("', ");
        sql.append(clientUser.getEmail() == null ? null : "'" + clientUser.getEmail() + "'").append(", ");
        sql.append(encryptForUpdate(clientUser.getPassword())).append(", ");
        sql.append(encryptForUpdate(clientUser.getSmsKey())).append(");");

        return getQueryAgent().executeInsert(sql.toString(), "client_user_id");
    }

    public int updateSmsKey(String smsKey, String phone) throws Exception {
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE client_user");
        sql.append(" SET sms_key = ").append(encryptForUpdate(smsKey));
        sql.append(" WHERE client_id = (SELECT client_id FROM client WHERE phone = '").append(phone).append("');");
        return getQueryAgent().executeUpdate(sql.toString());
    }

    public ClientUser getByClientId(final long clientId) throws Exception {
        return getQueryAgent().selectObject(new DbBuilder<ClientUser>() {
            @Override
            public String sql() {
                StringBuilder sql = new StringBuilder();
                sql.append("SELECT client_user.* FROM ").append("client_user");
                sql.append(" WHERE client_id = ").append(clientId).append(";");
                return sql.toString();
            }

            @Override
            public ClientUser build(ResultSet resultSet) throws SQLException {
                ClientUser clientUser = new ClientUser();
                clientUser.setClientUserId(resultSet.getLong("client_user_id"));
                clientUser.setClientId(resultSet.getLong("client_id"));
                clientUser.setName(resultSet.getString("name"));
                clientUser.setEmail(resultSet.getString("email"));
                clientUser.setPassword(resultSet.getString("password"));
                clientUser.setSmsKey(resultSet.getString("sms_key"));
                return clientUser;
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
                sql.append(" WHERE client.phone = '").append(phone).append("'");
                sql.append(" AND client_user.sms_key = ").append(encryptForSelect("sms_key", smsKey));
                return sql.toString();
            }

            @Override
            public ClientUser build(ResultSet resultSet) throws SQLException {
                ClientUser clientUser = new ClientUser();
                clientUser.setClientUserId(resultSet.getLong("client_user_id"));
                clientUser.setClientId(resultSet.getLong("client_id"));
                clientUser.setName(resultSet.getString("name"));
                clientUser.setEmail(resultSet.getString("email"));
                clientUser.setPassword(resultSet.getString("password"));
                clientUser.setSmsKey(resultSet.getString("sms_key"));
                return clientUser;
            }
        });
    }

    public ClientUser getByEmailAndPassword(final String email, final String password) throws Exception {
        return getQueryAgent().selectObject(new DbBuilder<ClientUser>() {
            @Override
            public String sql() {
                StringBuilder sql = new StringBuilder();
                sql.append("SELECT client_user.* FROM ").append("client_user");
                sql.append(" WHERE client_user.email = '").append(email).append("'");
                sql.append(" AND client_user.password = ").append(encryptForSelect(password, "password"));
                return sql.toString();
            }

            @Override
            public ClientUser build(ResultSet resultSet) throws SQLException {
                ClientUser clientUser = new ClientUser();
                clientUser.setClientUserId(resultSet.getLong("client_user_id"));
                clientUser.setClientId(resultSet.getLong("client_id"));
                clientUser.setName(resultSet.getString("name"));
                clientUser.setEmail(resultSet.getString("email"));
                clientUser.setPassword(resultSet.getString("password"));
                clientUser.setSmsKey(resultSet.getString("sms_key"));
                return clientUser;
            }
        });
    }

}
