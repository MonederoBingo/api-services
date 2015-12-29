package com.lealpoints.repository.fixtures;

public class PointsConfigurationRepositoryFixture {

    public static final String INSERT_COMPANY_AND_POINTS_CONFIGURATION = "INSERT INTO company (company_id, name, url_image_logo) VALUES (1, 'X', 'Y');\n" +
                "INSERT INTO points_configuration (points_configuration_id, company_id, points_to_earn, required_amount) VALUES (1, 1, 0, 0);";

    public static final String INSERT_COMPANY = "INSERT INTO company (company_id, name, url_image_logo) VALUES (1, 'X', 'Y');";

}
