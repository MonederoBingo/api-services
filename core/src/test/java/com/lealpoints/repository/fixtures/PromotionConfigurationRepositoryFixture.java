package com.lealpoints.repository.fixtures;

public class PromotionConfigurationRepositoryFixture {

    public String insertCompanyAndPromotionConfiguration()
    {
        return "INSERT INTO company (company_id, name, url_image_logo) VALUES (1, 'X', 'Y');\n" +
                "INSERT INTO promotion_configuration (promotion_configuration_id, company_id, description, required_points) VALUES (1, 1, '10% off', 1200);";
    }

    public String insertCompanyAndTwoPromotionConfiguration()
    {
        return "INSERT INTO company (company_id, name, url_image_logo) VALUES (1, 'X', 'Y');\n" +
                "INSERT INTO promotion_configuration (promotion_configuration_id, company_id, description, required_points) VALUES (1, 1, '10% off', 1200);\n" +
                "INSERT INTO promotion_configuration (promotion_configuration_id, company_id, description, required_points) VALUES (2, 1, '20% off', 2400);";
    }

    public String insertCompany()
    {
        return "INSERT INTO company (company_id, name, url_image_logo) VALUES (1, 'X', 'Y');";
    }
}
