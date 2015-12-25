package com.lealpoints.repository.fixture;

public class CompanyClientMappingRepositoryFixture {

    public String getFixturefortestGetByCompanyIdClientId() {
        return "INSERT INTO company(company_id, name, url_image_logo) VALUES(1, 'X','Y');\n" +
                "INSERT INTO client(client_id, phone, can_receive_promo_sms) VALUES(1, 'Y', TRUE);\n" +
                "INSERT INTO company_client_mapping (company_client_mapping_id, company_id, client_id) VALUES (1, 1, 1);";
    }

    public String getFixturefortestInsert() {
        return "INSERT INTO company (company_id, name, url_image_logo) VALUES (1, 'X', 'Y');\n" +
                "INSERT INTO client (client_id, phone, can_receive_promo_sms) VALUES (1, 'Y', TRUE);";
    }

    public String getFixturefortestInsertViolatingUnique() {
        return "INSERT INTO company (company_id, name, url_image_logo) VALUES (1, 'X', 'Y');\n" +
                "INSERT INTO client (client_id, phone, can_receive_promo_sms) VALUES (1, 'Y', TRUE);";
    }

    public String getFixturefortestInsertIfDoesNotExistWhenDoNot() {
        return "INSERT INTO company (company_id, name, url_image_logo) VALUES (1, 'X', 'Y');\n" +
                "INSERT INTO client (client_id, phone, can_receive_promo_sms) VALUES (1, 'Y', TRUE);";
    }

    public String getFixturefortestInsertIfDoesNotExistWhenDoes() {
        return "INSERT INTO company (company_id, name, url_image_logo) VALUES (1, 'X', 'Y');\n" +
                "INSERT INTO client (client_id, phone, can_receive_promo_sms) VALUES (1, 'Y', TRUE);\n" +
                "INSERT INTO company_client_mapping (company_client_mapping_id, company_id, client_id) VALUES (1, 1, 1);";
    }

    public String getFixturefortestUpdatePoints() {
        return "INSERT INTO company (company_id, name, url_image_logo) VALUES (1, 'A', 'logo');\n" +
                "INSERT INTO client (client_id, phone, can_receive_promo_sms) VALUES (1, 'A', TRUE);\n" +
                "INSERT INTO company_client_mapping (company_client_mapping_id, company_id, client_id, points) VALUES (1, 1, 1, 10);";
    }
}
