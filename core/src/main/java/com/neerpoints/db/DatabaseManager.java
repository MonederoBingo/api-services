package com.neerpoints.db;

public interface DatabaseManager {

    String getUrl();

    String getUrlWithoutDatabase();

    String getDriver();

    String getUser();

    String getPassword();
}
