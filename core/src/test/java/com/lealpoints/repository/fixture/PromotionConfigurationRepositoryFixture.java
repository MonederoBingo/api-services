package com.lealpoints.repository.fixture;

public class PromotionConfigurationRepositoryFixture {

    public String getFixturefortestGetById()
    {
        return "INSERT INTO company (company_id, name, url_image_logo) VALUES (1, 'X', 'Y');\n" +
                "INSERT INTO promotion_configuration (promotion_configuration_id, company_id, description, required_points) VALUES (1, 1, '10% off', 1200);";
    }

    public String getFixturefortestGetByCompanyId()
    {
        return "INSERT INTO company (company_id, name, url_image_logo) VALUES (1, 'X', 'Y');\n" +
                "INSERT INTO promotion_configuration (promotion_configuration_id, company_id, description, required_points) VALUES (1, 1, '10% off', 1200);\n" +
                "INSERT INTO promotion_configuration (promotion_configuration_id, company_id, description, required_points) VALUES (2, 1, '20% off', 2400);";
    }

    public String getFixturefortestInser()
    {
        return "INSERT INTO company (company_id, name, url_image_logo) VALUES (1, 'X', 'Y');";
    }

    public String getFixturefortestDelete()
    {
        return "INSERT INTO company (company_id, name, url_image_logo) VALUES (1, 'X', 'Y');\n" +
                "INSERT INTO promotion_configuration (promotion_configuration_id, company_id, description, required_points) VALUES (1, 1, '10% off', 1200);";
    }
}
