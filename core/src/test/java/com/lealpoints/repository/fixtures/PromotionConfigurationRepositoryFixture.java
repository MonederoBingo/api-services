package com.lealpoints.repository.fixtures;

public class PromotionConfigurationRepositoryFixture {

    public static final String INSERT_COMPANY_ANDO_PROMOTION_CONFIGURATION =
            "INSERT INTO company (company_id, name, url_image_logo) VALUES (1, 'X', 'Y');" +
                    "INSERT INTO promotion_configuration (promotion_configuration_id, company_id, description, required_points) " +
                    "VALUES (1, 1, '10% off', 1200);";

    public static final String INSERT_COMPANY_AND_TWO_PROMOTION_CONFIGURATION =
            "INSERT INTO company (company_id, name, url_image_logo) VALUES (1, 'X', 'Y');" +
                    "INSERT INTO promotion_configuration (promotion_configuration_id, company_id, description, required_points) " +
                    "VALUES (1, 1, '10% off', 1200);" +
                    "INSERT INTO promotion_configuration (promotion_configuration_id, company_id, description, required_points) " +
                    "VALUES (2, 1, '20% off', 2400);";

    public static final String INSERT_COMPANY =
            "INSERT INTO company (company_id, name, url_image_logo) VALUES (1, 'X', 'Y');";
}
