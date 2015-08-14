package com.lealpoints.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import com.lealpoints.db.DbBuilder;
import com.lealpoints.model.CompanyUser;
import org.springframework.stereotype.Component;

@Component
public class CompanyUserRepository extends BaseRepository {

    public long insert(CompanyUser companyUser) throws Exception {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO company_user(company_id, name, email, password, active, activation_key, language, must_change_password)");
        sql.append(" VALUES (");
        sql.append(companyUser.getCompanyId()).append(", ");
        sql.append("'").append(companyUser.getName()).append("', ");
        sql.append("'").append(companyUser.getEmail()).append("', ");
        sql.append(encryptForUpdate(companyUser.getPassword())).append(", ");
        sql.append("'").append(companyUser.isActive()).append("', ");
        sql.append("'").append(companyUser.getActivationKey()).append("', ");
        sql.append("'").append(companyUser.getLanguage()).append("', ");
        sql.append("'").append(companyUser.getMustChangePassword()).append("');");

        return getQueryAgent().executeInsert(sql.toString(), "company_user_id");
    }

    public CompanyUser getByEmailAndPassword(final String email, final String password) throws Exception {
        return getQueryAgent().selectObject(new DbBuilder<CompanyUser>() {
            @Override
            public String sql() {
                StringBuilder sql = new StringBuilder();
                sql.append("SELECT company_user.* FROM ").append("company_user");
                sql.append(" WHERE company_user.email = '").append(email).append("'");
                sql.append(" AND company_user.password = ").append(encryptForSelect("password", password));
                return sql.toString();
            }

            @Override
            public CompanyUser build(ResultSet resultSet) throws SQLException {
                return buildCompanyUser(resultSet);
            }
        });
    }

    public CompanyUser getByEmail(final String email) throws Exception {
        return getQueryAgent().selectObject(new DbBuilder<CompanyUser>() {
            @Override
            public String sql() {
                StringBuilder sql = new StringBuilder();
                sql.append("SELECT company_user.* FROM company_user");
                sql.append(" WHERE company_user.email = '").append(email).append("';");
                return sql.toString();
            }

            @Override
            public CompanyUser build(ResultSet resultSet) throws SQLException {
                return buildCompanyUser(resultSet);
            }
        });
    }

    public int updateActivateByActivationKey(final String activationKey) throws Exception {
        return getQueryAgent().executeUpdate("UPDATE company_user SET active = true WHERE activation_key = '" + activationKey + "';");
    }

    public int updatePasswordByEmail(final String email, final String password, final boolean mustChangePassword) throws Exception {
        return getQueryAgent().executeUpdate(
            "UPDATE company_user SET password = " + encryptForUpdate(password) + ", must_change_password = " + mustChangePassword +
                " WHERE email = '" + email + "';");
    }

    public int updateApiKeyByEmail(final String email, final String apiKey) throws Exception {
        return getQueryAgent().executeUpdate("UPDATE company_user SET api_key =  " + encryptForUpdate(apiKey) + " WHERE email = '" + email + "';");
    }

    public int clearActivationKey(final String activationKey) throws Exception {
        return getQueryAgent().executeUpdate("UPDATE company_user SET activation_key = NULL WHERE activation_key = '" + activationKey + "';");
    }

    public CompanyUser getByCompanyUserIdApiKey(final String companyUserId, final String apiKey) throws Exception {
        return getQueryAgent().selectObject(new DbBuilder<CompanyUser>() {
            @Override
            public String sql() {
                StringBuilder sql = new StringBuilder();
                sql.append("SELECT company_user.* FROM company_user");
                sql.append(" WHERE company_user.company_user_id = ").append(companyUserId);
                sql.append(" AND company_user.api_key = ").append(encryptForSelect("api_key", apiKey)).append(";");
                return sql.toString();
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
}
