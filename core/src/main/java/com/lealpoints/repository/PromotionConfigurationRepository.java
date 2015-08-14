package com.lealpoints.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import com.lealpoints.db.DbBuilder;
import com.lealpoints.model.PromotionConfiguration;
import org.springframework.stereotype.Component;

@Component
public class PromotionConfigurationRepository extends BaseRepository {

    public PromotionConfiguration getById(final long id) throws Exception {
        return getQueryAgent().selectObject(new DbBuilder<PromotionConfiguration>() {
            @Override
            public String sql() {
                return "SELECT * FROM promotion_configuration WHERE promotion_configuration_id = " + id + ";";
            }

            @Override
            public PromotionConfiguration build(ResultSet resultSet) throws SQLException {
                PromotionConfiguration promotionConfiguration = new PromotionConfiguration();
                promotionConfiguration.setPromotionConfigurationId(resultSet.getLong("promotion_configuration_id"));
                promotionConfiguration.setCompanyId(resultSet.getLong("company_id"));
                promotionConfiguration.setDescription(resultSet.getString("description"));
                promotionConfiguration.setRequiredPoints(resultSet.getFloat("required_points"));
                return promotionConfiguration;
            }
        });
    }

    public List<PromotionConfiguration> getByCompanyId(final long companyId) throws Exception {
        return getQueryAgent().selectList(new DbBuilder<PromotionConfiguration>() {
            @Override
            public String sql() {
                return "SELECT * FROM promotion_configuration WHERE company_id = " + companyId + ";";
            }

            @Override
            public PromotionConfiguration build(ResultSet resultSet) throws SQLException {
                PromotionConfiguration promotionConfiguration = new PromotionConfiguration();
                promotionConfiguration.setPromotionConfigurationId(resultSet.getLong("promotion_configuration_id"));
                promotionConfiguration.setCompanyId(resultSet.getLong("company_id"));
                promotionConfiguration.setDescription(resultSet.getString("description"));
                promotionConfiguration.setRequiredPoints(resultSet.getFloat("required_points"));
                return promotionConfiguration;
            }
        });
    }

    public long insert(PromotionConfiguration promotionConfiguration) throws Exception {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO promotion_configuration(company_id, description, required_points)");
        sql.append(" VALUES (");
        sql.append(promotionConfiguration.getCompanyId()).append(", ");
        sql.append("'").append(promotionConfiguration.getDescription()).append("', ");
        sql.append(promotionConfiguration.getRequiredPoints()).append(");");

        return getQueryAgent().executeInsert(sql.toString(), "promotion_configuration_id");
    }

    public int delete(long promotionConfigurationId) throws Exception {
        StringBuilder sql = new StringBuilder();
        sql.append("DELETE FROM promotion_configuration");
        sql.append(" WHERE promotion_configuration_id = ").append(promotionConfigurationId).append(";");
        return getQueryAgent().executeUpdate(sql.toString());
    }
}
