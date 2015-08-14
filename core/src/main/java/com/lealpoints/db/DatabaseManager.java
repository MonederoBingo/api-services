package com.lealpoints.db;

public interface DatabaseManager {

    String getUrl();

    String getUrlWithoutDatabase();

    String getDriver();

    String getUser();

    String getPassword();
}
