package com.lealpoints.repository.fixtures;

public class ClientUserRepositoryFixture {
    public static final String INSERT_CLIENT =
            "INSERT INTO client(client_id, phone, can_receive_promo_sms) VALUES(1, '6141112233', TRUE);";

    public static final String INSERT_CLIENT_THAT_CAN_RECEIVE_SMS =
            "INSERT INTO client (client_id, phone, can_receive_promo_sms) VALUES (1, '6141112233', TRUE);" +
                    "INSERT INTO client_user (client_user_id, client_id, name, email, password, sms_key, api_key)" +
                    "VALUES (1, 1, 'name', 'a@a.com', crypt('password', gen_salt('bf')), " +
                    "crypt('qwerty', gen_salt('bf')), crypt('ASDQWE', gen_salt('bf')));";

    public static final String INSERT_CLIENT_AND_CLIENT_USER =
            "INSERT INTO client(client_id, phone, can_receive_promo_sms) VALUES(1, '6141112233', TRUE);" +
                    "INSERT INTO client_user (client_user_id, client_id, name, email, password, sms_key, api_key)" +
                    "VALUES (1, 1, 'name', 'a@a.com', crypt('password', gen_salt('bf')), " +
                    "crypt('qwerty', gen_salt('bf')), crypt('ASDQWE', gen_salt('bf')));";
}
