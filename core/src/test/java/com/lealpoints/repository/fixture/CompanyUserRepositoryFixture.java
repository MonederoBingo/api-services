package com.lealpoints.repository.fixture;

public class CompanyUserRepositoryFixture {

    public String getFixturefortestInsert() {
        return "INSERT INTO company(company_id, name, url_image_logo) VALUES(1, 'X','Y');";
    }

    public String getFixturefortestGetByEmailAndPassword() {
        return "INSERT INTO company(company_id, name, url_image_logo) VALUES(1, 'X','Y');\n" +
                "INSERT INTO company_user (company_user_id, company_id, name, email, password, active, activation_key, language, must_change_password, api_key)\n" +
                "VALUES (1, 1, 'name', 'a@a.com', crypt('password', gen_salt('bf')), TRUE, '1234', 'es', FALSE, crypt('ASDQWE', gen_salt('bf')));";
    }

    public String getFixturefortestGetByEmail() {
        return "INSERT INTO company(company_id, name, url_image_logo) VALUES(1, 'X','Y');\n" +
                "INSERT INTO company_user (company_user_id, company_id, name, email, password, active, activation_key, language, must_change_password, api_key)\n" +
                "VALUES (1, 1, 'name', 'a@a.com', crypt('password', gen_salt('bf')), TRUE, '1234', 'es', FALSE, crypt('ASDQWE', gen_salt('bf')));";
    }

    public String getFixturefortestUpdateActivateByActivationKey() {
        return "INSERT INTO company (company_id, name, url_image_logo) VALUES (1, 'X', 'Y');\n" +
                "INSERT INTO company_user (company_user_id, company_id, name, email, password, active, activation_key, must_change_password, api_key)\n" +
                "VALUES (1, 1, 'name', 'a@a.com', crypt('password', gen_salt('bf')), FALSE, '1234', FALSE, 'ASDF');";
    }

    public String getFixturefortestClearActivationKey() {
        return "INSERT INTO company (company_id, name, url_image_logo) VALUES (1, 'X', 'Y');\n" +
                "INSERT INTO company_user (company_user_id, company_id, name, email, password, active, activation_key, must_change_password, api_key)\n" +
                "VALUES (1, 1, 'name', 'a@a.com', crypt('password', gen_salt('bf')), FALSE, '1234', FALSE, 'ASDF');";
    }

    public String getFixturefortestSetTempPasswordByEmail() {
        return "INSERT INTO company (company_id, name, url_image_logo) VALUES (1, 'X', 'Y');\n" +
                "INSERT INTO company_user (company_user_id, company_id, name, email, password, active, activation_key, must_change_password, api_key)\n" +
                "VALUES (1, 1, 'name', 'a@a.com', crypt('password', gen_salt('bf')), FALSE, '1234', FALSE, 'ASDF');";
    }

    public String getFixturefortestUpdatePasswordByEmail() {
        return "INSERT INTO company (company_id, name, url_image_logo) VALUES (1, 'X', 'Y');\n" +
                "INSERT INTO company_user (company_user_id, company_id, name, email, password, active, activation_key, must_change_password, api_key)\n" +
                "VALUES (1, 1, 'name', 'a@a.com', crypt('password', gen_salt('bf')), FALSE, '1234', FALSE, 'ASDF');";
    }

    public String getFixturefortestApiKeyByEmail() {
        return "INSERT INTO company (company_id, name, url_image_logo) VALUES (1, 'X', 'Y');\n" +
                "INSERT INTO company_user (company_user_id, company_id, name, email, password, active, activation_key, must_change_password, api_key)\n" +
                "VALUES (1, 1, 'name', 'a@a.com', crypt('password', gen_salt('bf')), FALSE, '1234', FALSE, 'ASDF');";
    }

    public String getFixturefortestGetByCompanyUserIdApiKey() {
        return "INSERT INTO company(company_id, name, url_image_logo) VALUES(1, 'X','Y');\n" +
                "INSERT INTO company_user (company_user_id, company_id, name, email, password, active, activation_key, language, must_change_password, api_key)\n" +
                "VALUES (1, 1, 'name', 'a@a.com', crypt('password', gen_salt('bf')), TRUE, '1234', 'es', FALSE, crypt('ASDQWE', gen_salt('bf')));";
    }
}
