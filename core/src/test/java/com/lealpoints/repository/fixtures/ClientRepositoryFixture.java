package com.lealpoints.repository.fixtures;

public class ClientRepositoryFixture {

    public String insertCompanyTwoClientsAndTwoMapping() {
        return "INSERT INTO company(company_id, name, url_image_logo) VALUES(1, 'X','Y');\n" +
                "INSERT INTO client(client_id, phone, can_receive_promo_sms) VALUES(1, 'A', TRUE);\n" +
                "INSERT INTO client(client_id, phone, can_receive_promo_sms) VALUES(2, 'X', TRUE);\n" +
                "INSERT INTO company_client_mapping(company_id, client_id, points) VALUES(1, 1, 100);\n" +
                "INSERT INTO company_client_mapping(company_id, client_id, points) VALUES(1, 2, 200);";
    }

    public String insertCompanyClientAndMapping() {
        return "INSERT INTO company (company_id, name, url_image_logo) VALUES (1, 'X', 'Y');\n" +
                "    INSERT INTO client (client_id, phone, can_receive_promo_sms) VALUES (1, '1234567890', TRUE);\n" +
                "    INSERT INTO company_client_mapping (company_id, client_id, points) VALUES (1, 1, 1200);";
    }

    public String insertTwoClients() {
        return "INSERT INTO client(client_id, phone, can_receive_promo_sms) VALUES(1, '1234', TRUE);\n" +
                "INSERT INTO client(client_id, phone, can_receive_promo_sms) VALUES(2, '5678', TRUE);";
    }

    public String insertClient() {
        return "INSERT INTO client(client_id, phone, can_receive_promo_sms) VALUES(1, '1234', TRUE);";
    }

    public String insertClientThatCanReceiveSMS() {
        return "INSERT INTO client (client_id, phone, can_receive_promo_sms) VALUES (1, '6141112233', TRUE);";
    }
}
