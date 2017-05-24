package com.lealpoints.repository;

import com.lealpoints.DatabaseServiceResult;
import com.lealpoints.db.util.DbBuilder;
import com.lealpoints.model.CompanyUser;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class CompanyUserRepository extends BaseRepository {

    public long insert(CompanyUser companyUser) throws Exception {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO company_user(company_id, name, email, " +
                "password, active, activation_key, language, must_change_password)");
        sql.append(" VALUES (");
        sql.append(companyUser.getCompanyId()).append(", ");
        sql.append("'").append(companyUser.getName()).append("', ");
        sql.append("'").append(companyUser.getEmail()).append("', ");
        sql.append(encryptForUpdate(companyUser.getPassword())).append(", ");
        sql.append("'").append(companyUser.isActive()).append("', ");
        sql.append("'").append(companyUser.getActivationKey()).append("', ");
        sql.append("'").append(companyUser.getLanguage()).append("', ");
        sql.append("'").append(companyUser.getMustChangePassword()).append("');");

        HttpEntity<InsertQuery> entity = new HttpEntity<>(
                new InsertQuery(sql.toString(), "company_user_id"),
                getHttpHeaders());
        ResponseEntity<DatabaseServiceResult> responseEntity = getRestTemplate().postForEntity(
                "http://test.localhost:30001/insert",
                entity,
                DatabaseServiceResult.class);
        if(responseEntity.getBody().getObject() == null)
        {
            return 0L;
        }
        return Long.parseLong(responseEntity.getBody().getObject().toString());
    }

    public List<CompanyUser> getByCompanyId(final long companyId) throws Exception {
        return getQueryAgent().selectList(new DbBuilder<CompanyUser>() {
            @Override
            public String sql() {return "SELECT * FROM company_user WHERE company_id = ? ;";}

            @Override
            public Object[] values() {
                return new Object[]{companyId};
            }

            @Override
            public CompanyUser build(ResultSet resultSet) throws SQLException {
                CompanyUser companyUser = new CompanyUser();
                companyUser.setCompanyId(resultSet.getLong("company_id"));
                companyUser.setCompanyUserId(resultSet.getLong("company_user_id"));
                companyUser.setActive(resultSet.getBoolean("active"));
                companyUser.setEmail(resultSet.getString("email"));
                companyUser.setName(resultSet.getString("name"));
                return companyUser;
            }
        });
    }

    public CompanyUser getByEmailAndPassword(final String email, final String password) throws Exception {
        return getQueryAgent().selectObject(new DbBuilder<CompanyUser>() {
            @Override
            public String sql() {
                StringBuilder sql = new StringBuilder();
                sql.append("SELECT company_user.* FROM ").append("company_user");
                sql.append(" WHERE company_user.email = ?");
                sql.append(" AND company_user.password = ").append(encryptForSelect("password", "?"));
                return sql.toString();
            }

            @Override
            public Object[] values() {
                return new Object[]{email, password};
            }

            @Override
            public CompanyUser build(ResultSet resultSet) throws SQLException {
                return buildCompanyUser(resultSet);
            }
        });
    }

    public CompanyUser getByEmail(final String email) throws Exception {
        getQueryAgent().selectObject(new DbBuilder<CompanyUser>() {
            @Override
            public String sql() {
                StringBuilder sql = new StringBuilder();
                sql.append("SELECT company_user.* FROM company_user");
                sql.append(" WHERE company_user.email = ? ;");
                return sql.toString();
            }

            @Override
            public Object[] values() {
                return new Object[]{email};
            }

            @Override
            public CompanyUser build(ResultSet resultSet) throws SQLException {
                return buildCompanyUser(resultSet);
            }
        });

        HttpEntity<SelectQuery> entity = new HttpEntity<>(
                new SelectQuery("select company_user.* FROM company_user WHERE company_user.email = '" + email + "' ;"),
                getHttpHeaders());
        ResponseEntity<DatabaseServiceResult> responseEntity = getRestTemplate().postForEntity(
                "http://test.localhost:30001/select",
                entity,
                DatabaseServiceResult.class);
        if(responseEntity.getBody().getObject() == null)
        {
            return null;
        }
        return buildCompanyUser(new JSONObject(responseEntity.getBody()).getString("object"));
    }

    private HttpHeaders getHttpHeaders()
    {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private RestTemplate getRestTemplate()
    {
        RestTemplate restTemplate = new RestTemplate();
        List<HttpMessageConverter<?>> list = new ArrayList<>();
        list.add(new MappingJackson2HttpMessageConverter());
        restTemplate.setMessageConverters(list);
        return restTemplate;
    }

    private class SelectQuery
    {
        private final String query;

        private SelectQuery(String query)
        {
            this.query = query;
        }

        public String getQuery()
        {
            return query;
        }
    }

    private class InsertQuery
    {
        private final String query;
        private final String idColumnName;

        private InsertQuery(String query, String idColumnName)
        {
            this.query = query;
            this.idColumnName = idColumnName;
        }

        public String getQuery()
        {
            return query;
        }

        public String getIdColumnName()
        {
            return idColumnName;
        }
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

    public CompanyUser getByCompanyUserIdApiKey(final Integer companyUserId, final String apiKey) throws Exception {
        return getQueryAgent().selectObject(new DbBuilder<CompanyUser>() {
            @Override
            public String sql() {
                StringBuilder sql = new StringBuilder();
                sql.append("SELECT company_user.* FROM company_user");
                sql.append(" WHERE company_user.api_key = ").append(encryptForSelect("api_key", "?")).append(";");
                return sql.toString();
            }

            @Override
            public Object[] values() {
                return new Object[]{apiKey};
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

    private CompanyUser buildCompanyUser(String stringObject) throws SQLException {
        JSONObject object = new JSONObject(stringObject);
        CompanyUser companyUser = new CompanyUser();
        companyUser.setCompanyUserId(object.getLong("company_user_id"));
        companyUser.setCompanyId(object.getLong("company_id"));
        companyUser.setName(object.getString("name"));
        companyUser.setEmail(object.getString("email"));
        companyUser.setPassword(object.getString("password"));
        companyUser.setActive(object.getBoolean("active"));
        companyUser.setActivationKey(object.getString("activation_key"));
        companyUser.setLanguage(object.getString("language"));
        companyUser.setMustChangePassword(object.getBoolean("must_change_password"));
        companyUser.setApiKey(object.getString("api_key"));
        return companyUser;
    }
}
