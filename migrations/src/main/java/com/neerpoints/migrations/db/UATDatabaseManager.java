package com.neerpoints.migrations.db;

import java.sql.Connection;
import java.sql.DriverManager;

public class UATDatabaseManager implements DatabaseManager {

    protected static Connection _connection;

    protected String getUrl() {
        return "jdbc:postgresql://127.3.94.2:5432/uat";
    }

    protected String getUrlWithoutDatabase() {
        return "jdbc:postgresql://127.3.94.2:5432";
    }

    protected String getDriver() {
        return "org.postgresql.Driver";
    }

    protected String getUser() {
        return "adminbpgiuam";
    }

    protected String getPassword() {
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
        if (_connection == null) {
            String url = withDataBase ? getUrl() : getUrlWithoutDatabase();
            Class.forName(getDriver());
            _connection = DriverManager.getConnection(url, getUser(), getPassword());
        }
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
