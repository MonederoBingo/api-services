package com.lealpoints.repository.fixtures;

public class PointsRepositoryFixture {

    public static final String INSERT_COMPANY =
            "INSERT INTO company (company_id, name, url_image_logo) VALUES (1, 'X', 'Y');" +
                    "INSERT INTO client (client_id, phone, can_receive_promo_sms) VALUES (1, 'A', TRUE);";

    public static final String INSERT_COMPANY_CLIENT_AND_POINTS =
            "INSERT INTO company (company_id, name, url_image_logo) VALUES (1, 'X', 'Y');" +
                    "INSERT INTO client (client_id, phone, can_receive_promo_sms) VALUES (1, 'A', TRUE);" +
                    "INSERT INTO points (company_id, client_id, sale_key, sale_amount, points_to_earn, required_amount, earned_points, date)" +
                    "VALUES (1, 1, 'A123', 100, 10, 100, 10, now());";
}
