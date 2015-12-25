package com.lealpoints.repository.fixture;

public class ClientRepositoryFixture {

    public String getFixturefortestGetByCompanyId() {
        return "INSERT INTO company(company_id, name, url_image_logo) VALUES(1, 'X','Y');\n" +
                "INSERT INTO client(client_id, phone, can_receive_promo_sms) VALUES(1, 'A', TRUE);\n" +
                "INSERT INTO client(client_id, phone, can_receive_promo_sms) VALUES(2, 'X', TRUE);\n" +
                "INSERT INTO company_client_mapping(company_id, client_id, points) VALUES(1, 1, 100);\n" +
                "INSERT INTO company_client_mapping(company_id, client_id, points) VALUES(1, 2, 200);";
    }

    public String getFixturefortestGetByCompanyIdPhone() {
        return "INSERT INTO company (company_id, name, url_image_logo) VALUES (1, 'X', 'Y');\n" +
                "    INSERT INTO client (client_id, phone, can_receive_promo_sms) VALUES (1, '1234567890', TRUE);\n" +
                "    INSERT INTO company_client_mapping (company_id, client_id, points) VALUES (1, 1, 1200);";
    }

    public String getFixturefortestGetByPhone() {
        return "INSERT INTO client(client_id, phone, can_receive_promo_sms) VALUES(1, '1234', TRUE);\n" +
                "INSERT INTO client(client_id, phone, can_receive_promo_sms) VALUES(2, '5678', TRUE);";
    }

    public String getFixturefortestInsertIfDoesNotExistWhenDoes() {
        return "INSERT INTO client(client_id, phone, can_receive_promo_sms) VALUES(1, '1234', TRUE);";
    }

    public String getFixturefortestUpdateCanReceivePromoSms() {
        return "INSERT INTO client (client_id, phone, can_receive_promo_sms) VALUES (1, '6141112233', TRUE);";
    }
}
