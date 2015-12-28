package com.lealpoints.repository.fixtures;

public class PointsRepositoryFixture {

    public String insertCompany()
    {
        return "INSERT INTO company (company_id, name, url_image_logo) VALUES (1, 'X', 'Y');\n" +
                "INSERT INTO client (client_id, phone, can_receive_promo_sms) VALUES (1, 'A', TRUE);";
    }

    public String insertCompanyClientAndPoints()
    {
        return "INSERT INTO company (company_id, name, url_image_logo) VALUES (1, 'X', 'Y');\n" +
                "INSERT INTO client (client_id, phone, can_receive_promo_sms) VALUES (1, 'A', TRUE);\n" +
                "INSERT INTO points (company_id, client_id, sale_key, sale_amount, points_to_earn, required_amount, earned_points, date)\n" +
                "VALUES (1, 1, 'A123', 100, 10, 100, 10, now());";
    }
}
