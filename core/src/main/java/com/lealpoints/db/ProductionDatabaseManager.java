package com.lealpoints.db;

import org.springframework.stereotype.Component;

@Component
public class ProductionDatabaseManager implements DatabaseManager {

    @Override
    public String getUrl() {
        return "jdbc:postgresql://127.3.94.2:5432/services";
    }

    @Override
    public String getUrlWithoutDatabase() {
        return "jdbc:postgresql://127.3.94.2:5432";
    }

    @Override
    public String getDriver() {
        return "org.postgresql.Driver";
    }

    @Override
    public String getUser() {
        return "adminbpgiuam";
    }

    @Override
    public String getPassword() {
        return "VAmdITUgEGdg";
    }

}
