package com.lealpoints.repository.fixture;

public class ClientUserRepositoryFixture {
    public String getFixturefortestInsert() {
        return "INSERT INTO client(client_id, phone, can_receive_promo_sms) VALUES(1, '6141112233', TRUE);";
    }

    public String getFixturefortestUpdateSmsKey() {
        return "INSERT INTO client (client_id, phone, can_receive_promo_sms) VALUES (1, '6141112233', TRUE);\n" +
                "INSERT INTO client_user (client_user_id, client_id, name, email, password, sms_key, api_key)\n" +
                "VALUES (1, 1, 'name', 'a@a.com', crypt('password', gen_salt('bf')), crypt('qwerty', gen_salt('bf')), crypt('ASDQWE', gen_salt('bf')));";
    }

    public String getFixturefortestGetByClientId() {
        return "INSERT INTO client(client_id, phone, can_receive_promo_sms) VALUES(1, '6141112233', TRUE);\n" +
                "INSERT INTO client_user (client_user_id, client_id, name, email, password, sms_key, api_key)\n" +
                "VALUES (1, 1, 'name', 'a@a.com', crypt('password', gen_salt('bf')), crypt('qwerty', gen_salt('bf')), crypt('ASDQWE', gen_salt('bf')));";
    }

    public String getFixturefortestGetByPhoneAndKey() {
        return "INSERT INTO client(client_id, phone, can_receive_promo_sms) VALUES(1, '6141112233', TRUE);\n" +
                "INSERT INTO client_user (client_user_id, client_id, name, email, password, sms_key, api_key)\n" +
                "VALUES (1, 1, 'name', 'a@a.com', crypt('password', gen_salt('bf')), crypt('qwerty', gen_salt('bf')), crypt('ASDQWE', gen_salt('bf')));";
    }

    public String getFixturefortestGetByEmailAndPassword() {
        return "INSERT INTO client(client_id, phone, can_receive_promo_sms) VALUES(1, '6141112233', TRUE);\n" +
                "INSERT INTO client_user (client_user_id, client_id, name, email, password, sms_key, api_key)\n" +
                "VALUES (1, 1, 'name', 'a@a.com', crypt('password', gen_salt('bf')), crypt('qwerty', gen_salt('bf')), crypt('ASDQWE', gen_salt('bf')));";
    }

    public String getFixturefortestApiKeyByEmail() {
        return "INSERT INTO client (client_id, phone, can_receive_promo_sms) VALUES (1, '6141112233', TRUE);\n" +
                "INSERT INTO client_user (client_user_id, client_id, name, email, password, sms_key, api_key)\n" +
                "VALUES (1, 1, 'name', 'a@a.com', crypt('password', gen_salt('bf')), crypt('qwerty', gen_salt('bf')), crypt('ASDQWE', gen_salt('bf')));";
    }

    public String getFixturefortestGetByCompanyUserIdApiKey() {
        return "INSERT INTO client(client_id, phone, can_receive_promo_sms) VALUES(1, '6141112233', TRUE);\n" +
                "INSERT INTO client_user (client_user_id, client_id, name, email, password, sms_key, api_key)\n" +
                "VALUES (1, 1, 'name', 'a@a.com', crypt('password', gen_salt('bf')), crypt('qwerty', gen_salt('bf')), crypt('ASDQWE', gen_salt('bf')));";
    }
}
