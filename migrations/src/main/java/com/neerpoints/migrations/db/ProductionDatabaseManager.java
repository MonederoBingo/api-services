package com.neerpoints.migrations.db;

import java.sql.Connection;
import java.sql.DriverManager;

public class ProductionDatabaseManager implements DatabaseManager {


    protected static Connection _connection;

    public String getUrl() {
        return "jdbc:postgresql://127.13.137.2:5432/services";
    }

    public String getUrlWithoutDatabase() {
        return "jdbc:postgresql://127.13.137.2:5432";
    }

    public String getDriver() {
        return "org.postgresql.Driver";
    }

    public String getUser() {
        return "adminbpgiuam";
    }

    public String getPassword() {
        return "VAmdITUgEGdg";
    }

    /**
     * Creates a new connection to a postgres database
     *
     * @return The postgres database connection created
     * @throws Exception
     */
    @Override
    public Connection getConnection(boolean withDataBase) throws Exception {
        String url = withDataBase ? getUrl() : getUrlWithoutDatabase();
        Class.forName(getDriver());
        _connection = DriverManager.getConnection(url, getUser(), getPassword());
        return _connection;
    }

    public Connection getConnection() {
        try {
            return getConnection(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
