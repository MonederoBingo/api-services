package com.lealpoints.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import com.lealpoints.db.util.DbBuilder;
import com.lealpoints.model.Configuration;
import org.springframework.stereotype.Component;

@Component
public class ConfigurationRepository extends BaseRepository {

    public long insert(Configuration configuration) throws Exception {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO configuration(name, description, value)");
        sql.append(" VALUES (");
        sql.append("'").append(configuration.getName()).append("', ");
        sql.append("'").append(configuration.getDescription()).append("', ");
        sql.append("'").append(configuration.getValue()).append("');");

        return getQueryAgent().executeInsert(sql.toString(), "configuration_id");
    }

    public List<Configuration> getConfigurationList() throws Exception {
        return getQueryAgent().selectList(new DbBuilder<Configuration>() {
            @Override
            public String sql() {
                return "SELECT configuration_id, name, description, value from configuration";
            }

            @Override
            public Object[] values() {
                return new Object[0];
            }

            @Override
            public Configuration build(ResultSet resultSet) throws SQLException {
                Configuration configuration = new Configuration();
                configuration.setConfigurationId(resultSet.getLong("configuration_id"));
                configuration.setName(resultSet.getString("name"));
                configuration.setDescription(resultSet.getString("description"));
                configuration.setValue(resultSet.getString("value"));
                return configuration;
            }
        });
    }

    public String getValueByName(final String name) throws Exception {
        return getQueryAgent().selectObject(new DbBuilder<String>() {
            @Override
            public String sql() {
                return "SELECT value FROM configuration WHERE name = ?;";
            }

            @Override
            public Object[] values() {
                return new Object[]{name};
            }

            @Override
            public String build(ResultSet resultSet) throws SQLException {
                return resultSet.getString("value");
            }
        });
    }
}
