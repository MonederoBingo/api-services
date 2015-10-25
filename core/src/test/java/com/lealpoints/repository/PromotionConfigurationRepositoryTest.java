package com.lealpoints.repository;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import com.lealpoints.db.queryagent.QueryAgent;
import com.lealpoints.model.PromotionConfiguration;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PromotionConfigurationRepositoryTest extends BaseRepositoryTest {
    private PromotionConfigurationRepository _promotionConfigurationRepository;

    @Before
    public void setUp() throws Exception {
        try {
            _promotionConfigurationRepository = createPromotionsRepository(getQueryAgent());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetById() throws Exception {
        insertFixture("promotion_configuration_repository_get_by_id.sql");
        PromotionConfiguration promotionConfiguration = _promotionConfigurationRepository.getById(1);
        assertNotNull(promotionConfiguration);
        assertEquals("10% off", promotionConfiguration.getDescription());
        assertEquals(1200, promotionConfiguration.getRequiredPoints(), 0.00);
    }

    @Test
    public void testGetByIdWhenNotFound() throws Exception {
        PromotionConfiguration promotionConfiguration = _promotionConfigurationRepository.getById(1);
        assertNull(promotionConfiguration);
    }

    @Test
    public void testGetByCompanyId() throws Exception {
        insertFixture("promotion_configuration_repository_get_by_company_id.sql");
        List<PromotionConfiguration> promotionConfigurations = _promotionConfigurationRepository.getByCompanyId(1);
        assertNotNull(promotionConfigurations);
        assertEquals(2, promotionConfigurations.size());
        assertNotNull(promotionConfigurations.get(0));
        assertNotNull(promotionConfigurations.get(1));
        assertEquals("10% off", promotionConfigurations.get(0).getDescription());
        assertEquals(1200, promotionConfigurations.get(0).getRequiredPoints(), 0.00);
        assertEquals("20% off", promotionConfigurations.get(1).getDescription());
        assertEquals(2400, promotionConfigurations.get(1).getRequiredPoints(), 0.00);
    }

    @Test
    public void testInsert() throws Exception {
        insertFixture("promotion_configuration_repository_insert.sql");
        PromotionConfiguration expectedPromotionConfiguration = new PromotionConfiguration();
        expectedPromotionConfiguration.setCompanyId(1);
        expectedPromotionConfiguration.setDescription("10% off");
        expectedPromotionConfiguration.setRequiredPoints(1200);

        final long promotionId = _promotionConfigurationRepository.insert(expectedPromotionConfiguration);
        PromotionConfiguration actualPromotionConfiguration = getPromotionById(promotionId);
        assertEquals(expectedPromotionConfiguration.getCompanyId(), actualPromotionConfiguration.getCompanyId());
        assertEquals(expectedPromotionConfiguration.getDescription(), actualPromotionConfiguration.getDescription());
        assertEquals(expectedPromotionConfiguration.getRequiredPoints(), actualPromotionConfiguration.getRequiredPoints(), 0.00);
    }

    @Test
    public void testDelete() throws Exception {
        insertFixture("promotion_configuration_repository_get_by_id.sql");
        PromotionConfiguration promotionConfiguration = getPromotionById(1);
        assertNotNull(promotionConfiguration);
        final int deletedRows = _promotionConfigurationRepository.delete(1);
        assertEquals(1, deletedRows);
        promotionConfiguration = getPromotionById(1);
        assertNull(promotionConfiguration);
    }

    private PromotionConfiguration getPromotionById(long promotionId) throws Exception {
        PromotionConfiguration promotionConfiguration = null;
        try (Statement st = getQueryAgent().getConnection().createStatement()) {
            ResultSet resultSet = st.executeQuery("SELECT * FROM promotion_configuration WHERE promotion_configuration_id = " + promotionId);
            if (resultSet.next()) {
                promotionConfiguration = new PromotionConfiguration();
                promotionConfiguration.setPromotionConfigurationId(resultSet.getLong("promotion_configuration_id"));
                promotionConfiguration.setCompanyId(resultSet.getLong("company_id"));
                promotionConfiguration.setDescription(resultSet.getString("description"));
                promotionConfiguration.setRequiredPoints(resultSet.getFloat("required_points"));
            }
        }
        return promotionConfiguration;
    }

    private PromotionConfigurationRepository createPromotionsRepository(final QueryAgent queryAgent) {
        return new PromotionConfigurationRepository() {
            @Override
            protected QueryAgent getQueryAgent() throws Exception {
                return queryAgent;
            }
        };
    }
}