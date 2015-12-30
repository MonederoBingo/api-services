package com.lealpoints.repository.fixtures;

public class ClientRepositoryFixture {

    public static final String INSERT_COMPANY_TWO_CLIENTS_AND_MAPPING =
            "INSERT INTO company(company_id, name, url_image_logo) VALUES(1, 'X','Y');" +
                    "INSERT INTO client(client_id, phone, can_receive_promo_sms) VALUES(1, 'A', TRUE);" +
                    "INSERT INTO client(client_id, phone, can_receive_promo_sms) VALUES(2, 'X', TRUE);" +
                    "INSERT INTO company_client_mapping(company_id, client_id, points) VALUES(1, 1, 100);" +
                    "INSERT INTO company_client_mapping(company_id, client_id, points) VALUES(1, 2, 200);";

    public static final String INSERT_COMPANY_CLIENT_AND_MAPPING =
            "INSERT INTO company (company_id, name, url_image_logo) VALUES (1, 'X', 'Y');" +
                    "INSERT INTO client (client_id, phone, can_receive_promo_sms) VALUES (1, '1234567890', TRUE);" +
                    "INSERT INTO company_client_mapping (company_id, client_id, points) VALUES (1, 1, 1200);";

    public static final String INSERT_TWO_CLIENTS =
            "INSERT INTO client(client_id, phone, can_receive_promo_sms) VALUES(1, '1234', TRUE);" +
                    "INSERT INTO client(client_id, phone, can_receive_promo_sms) VALUES(2, '5678', TRUE);";

    public static final String INSERT_CLIENT =
            "INSERT INTO client(client_id, phone, can_receive_promo_sms) VALUES(1, '1234', TRUE);";

    public static final String INSERT_CLIENT_THAT_CAN_RECEIVE_SMS =
            "INSERT INTO client (client_id, phone, can_receive_promo_sms) VALUES (1, '6141112233', TRUE);";
}
