package com.lealpoints.repository;

import com.lealpoints.model.Promotion;
import org.springframework.stereotype.Component;

@Component
public class PromotionRepository extends BaseRepository {
    public long insert(Promotion promotion) throws Exception {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO promotion(company_id, client_id, description, used_points, date)");
        sql.append(" VALUES (");
        sql.append(promotion.getCompanyId()).append(", ");
        sql.append(promotion.getClientId()).append(", ");
        sql.append("'").append(promotion.getDescription()).append("', ");
        sql.append(promotion.getUsedPoints()).append(", ");
        sql.append("'").append(promotion.getDate()).append("');");

        return getQueryAgent().executeInsert(sql.toString(), "promotion_id");
    }
}
