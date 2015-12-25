package com.lealpoints.repository.fixture;

public class PromotionRepositoryFixture {

    public String getFixturefortestInser()
    {
        return "INSERT INTO company (company_id, name, url_image_logo) VALUES (1, 'X', 'Y');\n" +
                "INSERT INTO client (client_id, phone, can_receive_promo_sms) VALUES (1, 'A', TRUE);";
    }
}
