package com.lealpoints.repository.fixture;

public class PointsConfigurationRepositoryFixture {

    public String getFixturefortestGetByCompanyId()
    {
        return "INSERT INTO company (company_id, name, url_image_logo) VALUES (1, 'X', 'Y');\n" +
                "INSERT INTO points_configuration (points_configuration_id, company_id, points_to_earn, required_amount) VALUES (1, 1, 0, 0);";
    }

    public String getFixturefortestInsert()
    {
        return "INSERT INTO company (company_id, name, url_image_logo) VALUES (1, 'X', 'Y');";
    }

    public String getFixturefortestUpdate()
    {
        return "INSERT INTO company (company_id, name, url_image_logo) VALUES (1, 'X', 'Y');\n" +
                "INSERT INTO points_configuration (points_configuration_id, company_id, points_to_earn, required_amount) VALUES (1, 1, 0, 0);";
    }
}
