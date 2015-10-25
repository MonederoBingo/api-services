package com.lealpoints.db.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

public interface SavepointProxyConnection extends Connection {
    boolean isClosed() throws SQLException;

    boolean isProxyConnectionActive();

    boolean getAutoCommit() throws SQLException;

    void close() throws SQLException;

    void beginTransactionForAutomationTest(String automationTestName) throws SQLException;

    void rollbackTransactionForAutomationTest() throws SQLException;

    String getConnectionUrl();

    void setConnectionUrl(String urlForWrappedDriver);
}
