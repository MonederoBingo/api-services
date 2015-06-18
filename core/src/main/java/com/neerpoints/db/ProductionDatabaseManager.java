package com.neerpoints.db;

import org.springframework.stereotype.Component;

@Component
public class ProductionDatabaseManager implements DatabaseManager {

    @Override
    public String getUrl() {
        return "jdbc:postgresql://127.13.137.2:5432/services";
    }

    @Override
    public String getUrlWithoutDatabase() {
        return "jdbc:postgresql://127.13.137.2:5432";
    }

    @Override
    public String getDriver() {
        return "org.postgresql.Driver";
    }

    @Override
    public String getUser() {
        return "adminkvmftl3";
    }

    @Override
    public String getPassword() {
        return "xA3W1Zi7pUW8";
    }

}
