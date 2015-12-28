package com.lealpoints.repository.fixtures;

public class ClientUserRepositoryFixture {
    public String insertClient() {
        return "INSERT INTO client(client_id, phone, can_receive_promo_sms) VALUES(1, '6141112233', TRUE);";
    }

    public String insertClientThatCanReceiveSMS() {
        return "INSERT INTO client (client_id, phone, can_receive_promo_sms) VALUES (1, '6141112233', TRUE);\n" +
                "INSERT INTO client_user (client_user_id, client_id, name, email, password, sms_key, api_key)\n" +
                "VALUES (1, 1, 'name', 'a@a.com', crypt('password', gen_salt('bf')), crypt('qwerty', gen_salt('bf')), crypt('ASDQWE', gen_salt('bf')));";
    }

    public String insertClientAndClientUser() {
        return "INSERT INTO client(client_id, phone, can_receive_promo_sms) VALUES(1, '6141112233', TRUE);\n" +
                "INSERT INTO client_user (client_user_id, client_id, name, email, password, sms_key, api_key)\n" +
                "VALUES (1, 1, 'name', 'a@a.com', crypt('password', gen_salt('bf')), crypt('qwerty', gen_salt('bf')), crypt('ASDQWE', gen_salt('bf')));";
    }
}
