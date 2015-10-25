package com.lealpoints.repository;

import java.sql.ResultSet;
import java.sql.Statement;
import com.lealpoints.db.queryagent.QueryAgent;
import com.lealpoints.model.Promotion;
import com.lealpoints.util.DateUtil;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PromotionRepositoryTest extends BaseRepositoryTest {
    private PromotionRepository _promotionRepository;

    @Before
    public void setUp() throws Exception {
        try {
            _promotionRepository = createPromotionRepository(getQueryAgent());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testInsert() throws Exception {
        insertFixture("promotion_repository_insert.sql");
        Promotion expectedPromotion = new Promotion();
        expectedPromotion.setCompanyId(1);
        expectedPromotion.setClientId(1);
        expectedPromotion.setDescription("key");
        expectedPromotion.setUsedPoints(1000);
        expectedPromotion.setDate(DateUtil.dateNow());

        final long promotionId = _promotionRepository.insert(expectedPromotion);
        Promotion actualPromotion = getPromotionById(promotionId);
        assertEquals(promotionId, actualPromotion.getPromotionId());
        assertEquals(expectedPromotion.getCompanyId(), actualPromotion.getCompanyId());
        assertEquals(expectedPromotion.getClientId(), actualPromotion.getClientId());
        assertEquals(expectedPromotion.getDescription(), actualPromotion.getDescription());
        assertEquals(expectedPromotion.getUsedPoints(), actualPromotion.getUsedPoints(), 0.00);
        assertEquals(DateUtil.formatDate(expectedPromotion.getDate(), "ddMMyyyy"), DateUtil.formatDate(actualPromotion.getDate(), "ddMMyyyy"));
    }

    private Promotion getPromotionById(long pointsId) throws Exception {
        Promotion points = new Promotion();
        try (Statement st = getQueryAgent().getConnection().createStatement()) {
            ResultSet resultSet = st.executeQuery("SELECT * FROM promotion WHERE promotion_id = " + pointsId);
            if (resultSet.next()) {
                points.setPromotionId(resultSet.getLong("promotion_id"));
                points.setCompanyId(resultSet.getLong("company_id"));
                points.setClientId(resultSet.getLong("client_id"));
                points.setDescription(resultSet.getString("description"));
                points.setUsedPoints(resultSet.getInt("used_points"));
                points.setDate(resultSet.getDate("date"));
            }
        }
        return points;
    }

    private PromotionRepository createPromotionRepository(final QueryAgent queryAgent) {
        return new PromotionRepository() {
            @Override
            protected QueryAgent getQueryAgent() throws Exception {
                return queryAgent;
            }
        };
    }
}