package com.neerpoints.db;

import org.springframework.stereotype.Component;

@Component
public class FunctionalTestDatabaseManager implements DatabaseManager {

    @Override
    public String getUrl() {
        return "jdbc:postgresql://localhost:5432/neerpoints_functional_test";
    }

    @Override
    public String getUrlWithoutDatabase() {
        return "jdbc:postgresql://localhost:5432";
    }

    @Override
    public String getDriver() {
        return "org.postgresql.Driver";
    }

    @Override
    public String getUser() {
        return "postgres";
    }

    @Override
    public String getPassword() {
        return "1234";
    }
}
