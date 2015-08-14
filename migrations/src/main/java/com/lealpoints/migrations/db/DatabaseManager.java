package com.lealpoints.migrations.db;

import java.sql.Connection;

/**
 * Defines a database manager methods
 */
public interface DatabaseManager {

    /**
     * Creates a new connection to a database
     *
     * @param withDataBase specifies if a database name must be used for getting the connection
     * @return The database connection created
     * @throws Exception
     */
    Connection getConnection(boolean withDataBase) throws Exception;

    Connection getConnection() throws Exception;
}