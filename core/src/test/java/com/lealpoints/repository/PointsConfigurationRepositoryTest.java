package com.lealpoints.repository;

import java.sql.ResultSet;
import java.sql.Statement;
import com.lealpoints.db.QueryAgent;
import com.lealpoints.model.PointsConfiguration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PointsConfigurationRepositoryTest extends BaseRepositoryTest {

    private PointsConfigurationRepository _pointsConfigurationRepository;

    @Before
    public void setUp() throws Exception {
        try {
            _pointsConfigurationRepository = createPointsConfigurationRepository(getQueryAgent());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetByCompanyId() throws Exception {
        insertFixture("points_configuration_repository_get_by_company_id.sql");
        PointsConfiguration pointsConfiguration = _pointsConfigurationRepository.getByCompanyId(1);
        assertNotNull(pointsConfiguration);
        assertEquals(0, pointsConfiguration.getPointsToEarn(), 0.00);
        assertEquals(0, pointsConfiguration.getRequiredAmount(), 0.00);
    }

    @Test
    public void testInsert() throws Exception {
        final int companyIdFromFixture = 1;
        insertFixture("points_configuration_repository_insert.sql");
        PointsConfiguration expectedPointsConfiguration = new PointsConfiguration();
        expectedPointsConfiguration.setCompanyId(companyIdFromFixture);
        expectedPointsConfiguration.setPointsToEarn(10);
        expectedPointsConfiguration.setRequiredAmount(100);
        final long pointsConfigurationId = _pointsConfigurationRepository.insert(expectedPointsConfiguration);

        PointsConfiguration actualPointsConfiguration = getPointsConfigurationById(pointsConfigurationId);
        assertEquals(pointsConfigurationId, actualPointsConfiguration.getPointsConfigurationId());
        assertEquals(expectedPointsConfiguration.getCompanyId(), actualPointsConfiguration.getCompanyId());
        assertEquals(expectedPointsConfiguration.getPointsToEarn(), actualPointsConfiguration.getPointsToEarn(), 0.00);
        assertEquals(expectedPointsConfiguration.getRequiredAmount(), actualPointsConfiguration.getRequiredAmount(), 0.00);
    }

    @Test
    public void testUpdate() throws Exception {
        final int companyIdFromFixture = 1;
        insertFixture("points_configuration_repository_get_by_company_id.sql");
        PointsConfiguration expectedPointsConfiguration = getPointFirstConfigurationByCompanyId();
        assertEquals(0, expectedPointsConfiguration.getPointsToEarn(), 0.00);
        assertEquals(0, expectedPointsConfiguration.getRequiredAmount(), 0.00);
        expectedPointsConfiguration.setCompanyId(companyIdFromFixture);
        expectedPointsConfiguration.setPointsToEarn(10);
        expectedPointsConfiguration.setRequiredAmount(100);
        int updatedRows = _pointsConfigurationRepository.update(expectedPointsConfiguration);

        PointsConfiguration actualPointsConfiguration = getPointFirstConfigurationByCompanyId();
        assertEquals(1, updatedRows);
        Assert.assertEquals(10, actualPointsConfiguration.getPointsToEarn(), 0.00);
        Assert.assertEquals(100, actualPointsConfiguration.getRequiredAmount(), 0.00);
    }

    private PointsConfiguration getPointFirstConfigurationByCompanyId() throws Exception {
        PointsConfiguration pointsConfiguration = new PointsConfiguration();
        try (Statement st = getQueryAgent().getConnection().createStatement()) {

            ResultSet resultSet = st.executeQuery("SELECT * FROM points_configuration;");
            if (resultSet.next()) {
                pointsConfiguration.setPointsConfigurationId(resultSet.getLong("points_configuration_id"));
                pointsConfiguration.setPointsToEarn(resultSet.getFloat("points_to_earn"));
                pointsConfiguration.setRequiredAmount(resultSet.getFloat("required_amount"));
            }
        }
        return pointsConfiguration;
    }

    private PointsConfiguration getPointsConfigurationById(long pointsConfigurationId) throws Exception {
        PointsConfiguration pointsConfiguration = new PointsConfiguration();
        try (Statement st = getQueryAgent().getConnection().createStatement()) {
            ResultSet resultSet = st.executeQuery("SELECT * FROM points_configuration WHERE points_configuration_id = " + pointsConfigurationId);
            if (resultSet.next()) {
                pointsConfiguration.setPointsConfigurationId(resultSet.getLong("points_configuration_id"));
                pointsConfiguration.setCompanyId(resultSet.getLong("company_id"));
                pointsConfiguration.setPointsToEarn(resultSet.getFloat("points_to_earn"));
                pointsConfiguration.setRequiredAmount(resultSet.getFloat("required_amount"));
            }
        }
        return pointsConfiguration;
    }

    private PointsConfigurationRepository createPointsConfigurationRepository(final QueryAgent queryAgent) {
        return new PointsConfigurationRepository() {
            @Override
            protected QueryAgent getQueryAgent() throws Exception {
                return queryAgent;
            }
        };
    }
}