package com.lealpoints.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import com.lealpoints.db.DbBuilder;
import com.lealpoints.model.Points;
import org.springframework.stereotype.Component;

@Component
public class PointsRepository extends BaseRepository {

    public long insert(Points points) throws Exception {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO points(company_id, client_id, sale_key, sale_amount, points_to_earn, required_amount, earned_points, date)");
        sql.append(" VALUES (");
        sql.append(points.getCompanyId()).append(", ");
        sql.append(points.getClientId()).append(", ");
        sql.append("'").append(points.getSaleKey()).append("', ");
        sql.append(points.getSaleAmount()).append(", ");
        sql.append(points.getPointsToEarn()).append(", ");
        sql.append(points.getRequiredAmount()).append(", ");
        sql.append(points.getEarnedPoints()).append(", ");
        sql.append("'").append(points.getDate()).append("');");

        return getQueryAgent().executeInsert(sql.toString(), "points_id");
    }

    public Points getByCompanyIdSaleKey(final long companyId, final String saleKey) throws Exception {
        return getQueryAgent().selectObject(new DbBuilder<Points>() {
            @Override
            public String sql() {
                return "SELECT * FROM points WHERE company_id = " + companyId + " AND sale_key = '" + saleKey + "';";
            }

            @Override
            public Points build(ResultSet resultSet) throws SQLException {
                Points points = new Points();
                points.setPointsId(resultSet.getLong("points_id"));
                points.setClientId(resultSet.getLong("client_id"));
                points.setCompanyId(resultSet.getLong("company_id"));
                points.setEarnedPoints(resultSet.getFloat("earned_points"));
                points.setPointsToEarn(resultSet.getFloat("points_to_earn"));
                points.setRequiredAmount(resultSet.getFloat("required_amount"));
                points.setDate(resultSet.getDate("date"));
                points.setSaleAmount(resultSet.getFloat("sale_amount"));
                points.setSaleKey(resultSet.getString("sale_key"));
                return points;
            }
        });
    }
}
