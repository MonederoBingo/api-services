package com.neerpoints.migrations.db;

import java.sql.Connection;
import java.sql.DriverManager;

public class DevelopmentDatabaseManager implements DatabaseManager {

    protected static Connection _connection;

    protected String getUrl() {
        return "jdbc:postgresql://localhost:5432/neerpoints";
    }

    protected String getUrlWithoutDatabase() {
        return "jdbc:postgresql://localhost:5432";
    }

    protected String getDriver() {
        return "org.postgresql.Driver";
    }

    protected String getUser() {
        return "postgres";
    }

    protected String getPassword() {
        return "1234";
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
