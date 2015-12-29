package com.lealpoints.repository;

import java.sql.ResultSet;
import java.sql.Statement;
import com.lealpoints.db.queryagent.QueryAgent;
import com.lealpoints.model.Points;
import com.lealpoints.util.DateUtil;
import org.junit.Before;
import org.junit.Test;

import static com.lealpoints.repository.fixtures.PointsRepositoryFixture.INSERT_COMPANY;
import static com.lealpoints.repository.fixtures.PointsRepositoryFixture.INSERT_COMPANY_CLIENT_AND_POINTS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PointsRepositoryTest extends BaseRepositoryTest {

    private PointsRepository _pointsRepository;

    @Before
    public void setUp() throws Exception {
        try {
            _pointsRepository = createPointsRepository(getQueryAgent());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testInsert() throws Exception {
        executeFixture(INSERT_COMPANY);
        Points expectedPoints = new Points();
        expectedPoints.setCompanyId(1);
        expectedPoints.setClientId(1);
        expectedPoints.setSaleKey("key");
        expectedPoints.setPointsToEarn(10);
        expectedPoints.setRequiredAmount(100);
        expectedPoints.setSaleAmount(100);
        expectedPoints.setEarnedPoints(10);
        expectedPoints.setDate(DateUtil.dateNow());
        final long pointsId = _pointsRepository.insert(expectedPoints);
        Points actualPoints = getPointsById(pointsId);
        assertEquals(pointsId, actualPoints.getPointsId());
        assertEquals(expectedPoints.getCompanyId(), actualPoints.getCompanyId());
        assertEquals(expectedPoints.getClientId(), actualPoints.getClientId());
        assertEquals(expectedPoints.getSaleKey(), actualPoints.getSaleKey());
        assertEquals(expectedPoints.getPointsToEarn(), actualPoints.getPointsToEarn(), 0.00);
        assertEquals(expectedPoints.getRequiredAmount(), actualPoints.getRequiredAmount(), 0.00);
        assertEquals(expectedPoints.getSaleAmount(), actualPoints.getSaleAmount(), 0.00);
        assertEquals(expectedPoints.getEarnedPoints(), actualPoints.getEarnedPoints(), 0.00);
        assertEquals(DateUtil.formatDate(expectedPoints.getDate(), "ddMMyyyy"), DateUtil.formatDate(actualPoints.getDate(), "ddMMyyyy"));
    }

    @Test
    public void testGetByCompanyId() throws Exception {
        executeFixture(INSERT_COMPANY_CLIENT_AND_POINTS);
        Points points = _pointsRepository.getByCompanyIdSaleKey(1, "A123");
        assertNotNull(points);
        assertEquals(1, points.getCompanyId());
        assertEquals(1, points.getClientId());
        assertEquals(100, points.getSaleAmount(), 0.00);
        assertEquals(10, points.getPointsToEarn(), 0.00);
        assertEquals(100, points.getRequiredAmount(), 0.00);
        assertEquals(10, points.getEarnedPoints(), 0.00);
    }

    private Points getPointsById(long pointsId) throws Exception {
        Points points = new Points();
        try (Statement st = getQueryAgent().getConnection().createStatement()) {
            ResultSet resultSet = st.executeQuery("SELECT * FROM points WHERE points_id = " + pointsId);
            if (resultSet.next()) {
                points.setPointsId(resultSet.getLong("points_id"));
                points.setCompanyId(resultSet.getLong("company_id"));
                points.setClientId(resultSet.getLong("client_id"));
                points.setSaleKey(resultSet.getString("sale_key"));
                points.setPointsToEarn(resultSet.getInt("points_to_earn"));
                points.setRequiredAmount(resultSet.getInt("required_amount"));
                points.setSaleAmount(resultSet.getFloat("sale_amount"));
                points.setEarnedPoints(resultSet.getFloat("earned_points"));
                points.setDate(resultSet.getDate("date"));
            }
        }
        return points;
    }

    private PointsRepository createPointsRepository(final QueryAgent queryAgent) {
        return new PointsRepository() {
            @Override
            protected QueryAgent getQueryAgent() throws Exception {
                return queryAgent;
            }
        };
    }
}