package com.lealpoints.repository.fixtures;

public class PromotionRepositoryFixture {

    public String insertCompanyAndClient()
    {
        return "INSERT INTO company (company_id, name, url_image_logo) VALUES (1, 'X', 'Y');\n" +
                "INSERT INTO client (client_id, phone, can_receive_promo_sms) VALUES (1, 'A', TRUE);";
    }
}
