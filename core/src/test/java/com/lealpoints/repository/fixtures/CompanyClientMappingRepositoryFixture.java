package com.lealpoints.repository.fixtures;

public class CompanyClientMappingRepositoryFixture {

    public String insertCompanyClientAndMapping() {
        return "INSERT INTO company(company_id, name, url_image_logo) VALUES(1, 'X','Y');\n" +
                "INSERT INTO client(client_id, phone, can_receive_promo_sms) VALUES(1, 'Y', TRUE);\n" +
                "INSERT INTO company_client_mapping (company_client_mapping_id, company_id, client_id) VALUES (1, 1, 1);";
    }

    public String insertCompanyAndClient() {
        return "INSERT INTO company (company_id, name, url_image_logo) VALUES (1, 'X', 'Y');\n" +
                "INSERT INTO client (client_id, phone, can_receive_promo_sms) VALUES (1, 'Y', TRUE);";
    }

    public String insertCompanyClientAndMappingWithPoints() {
        return "INSERT INTO company (company_id, name, url_image_logo) VALUES (1, 'A', 'logo');\n" +
                "INSERT INTO client (client_id, phone, can_receive_promo_sms) VALUES (1, 'A', TRUE);\n" +
                "INSERT INTO company_client_mapping (company_client_mapping_id, company_id, client_id, points) VALUES (1, 1, 1, 10);";
    }
}
