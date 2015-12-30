package com.lealpoints.repository;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import com.lealpoints.db.queryagent.QueryAgent;
import com.lealpoints.model.Configuration;
import org.junit.Before;
import org.junit.Test;

import static com.lealpoints.repository.fixtures.ConfigurationRepositoryFixture.INSERT_CONFIGURATION;
import static org.junit.Assert.*;

public class ConfigurationRepositoryTest extends BaseRepositoryTest {

    private ConfigurationRepository _configurationRepository;

    @Before
    public void setUp() throws Exception {
        try {
            _configurationRepository = createConfigurationRepository(getQueryAgent());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testInsert() throws Exception {
        Configuration configuration = new Configuration();
        configuration.setName("name1");
        configuration.setDescription("description1");
        configuration.setValue("value1");
        final long id = _configurationRepository.insert(configuration);
        final Configuration actualConfiguration = getConfigurationById(id);
        assertNotNull(configuration);
        assertEquals("name1", actualConfiguration.getName());
        assertEquals("description1", actualConfiguration.getDescription());
        assertEquals("value1", actualConfiguration.getValue());
    }

    @Test
    public void testGetAll() throws Exception {
        deleteConfigurationTable();
        executeFixture(INSERT_CONFIGURATION);
        final List<Configuration> configurationList = _configurationRepository.getConfigurationList();
        assertNotNull(configurationList);
        assertEquals(3, configurationList.size());
    }

    @Test
    public void testGetValueByName() throws Exception {
        executeFixture(INSERT_CONFIGURATION);
        final String value = _configurationRepository.getValueByName("name1");
        assertNotNull(value);
        assertEquals("value1", value);
        final String value2 = _configurationRepository.getValueByName("name10");
        assertNull(value2);
    }

    private Configuration getConfigurationById(long configurationId) throws Exception {
        Configuration configuration = new Configuration();
        try (Statement st = getQueryAgent().getConnection().createStatement()) {
            ResultSet resultSet = st.executeQuery("SELECT * FROM configuration WHERE configuration_id = " + configurationId);
            if (resultSet.next()) {
                configuration.setConfigurationId(resultSet.getLong("configuration_id"));
                configuration.setName(resultSet.getString("name"));
                configuration.setDescription(resultSet.getString("description"));
                configuration.setValue(resultSet.getString("value"));
            }
        }
        return configuration;
    }

    private void deleteConfigurationTable() throws Exception {
        try (Statement st = getQueryAgent().getConnection().createStatement()) {
            st.execute("DELETE FROM configuration");
        }
    }

    private ConfigurationRepository createConfigurationRepository(final QueryAgent queryAgent) {
        return new ConfigurationRepository() {
            @Override
            protected QueryAgent getQueryAgent() throws Exception {
                return queryAgent;
            }
        };
    }
}