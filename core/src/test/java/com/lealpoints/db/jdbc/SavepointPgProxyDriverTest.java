package com.lealpoints.db.jdbc;

import com.lealpoints.db.util.DbUtil;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import static org.junit.Assert.*;

public class SavepointPgProxyDriverTest {
    @BeforeClass
    public static void setUp() throws ClassNotFoundException {
        Class.forName("com.lealpoints.db.jdbc.SavepointPgProxyDriver");
    }

    @Test
    public void testAcceptsURL() throws Exception {
        SavepointPgProxyDriver savepointPgProxyDriver = getSavepointPgProxyDriver();
        assertFalse(savepointPgProxyDriver.acceptsURL("jdbc:postgresql://localhost/lealpoints"));
        assertFalse(savepointPgProxyDriver.acceptsURL("jdbc:postgresql://localhost/lealpoints_functional_test"));
        assertFalse(savepointPgProxyDriver.acceptsURL("xjdbc:savepointpgproxy://localhost/lealpoints"));

        assertTrue(savepointPgProxyDriver.acceptsURL("jdbc:savepointpgproxy://localhost/lealpoints"));
        assertTrue(savepointPgProxyDriver.acceptsURL("jdbc:savepointpgproxy://localhost/lealpoints_functional_test"));
    }

    private SavepointPgProxyDriver getSavepointPgProxyDriver() throws SQLException {
        return (SavepointPgProxyDriver) DriverManager.getDriver(getDriverUrl());
    }

    @Test
    public void testGetUrlForConnectionKey() throws Exception {
        SavepointPgProxyDriver savepointPgProxyDriver = getSavepointPgProxyDriver();
        assertEquals("jdbc:postgresql://localhost/lealpoints",
            savepointPgProxyDriver.getUrlForConnectionKey("jdbc:postgresql://localhost/lealpoints?compatible=7.4"));
        assertEquals("jdbc:postgresql://localhost/lealpoints",
            savepointPgProxyDriver.getUrlForConnectionKey("jdbc:postgresql://localhost/lealpoints?loglevel=2"));
        assertEquals("jdbc:savepointpgproxy://localhost/lealpoints_functional_test",
            savepointPgProxyDriver.getUrlForConnectionKey("jdbc:savepointpgproxy://localhost/lealpoints_functional_test?compatible=7.4"));
        assertEquals("jdbc:savepointpgproxy://localhost/lealpoints_functional_test",
            savepointPgProxyDriver.getUrlForConnectionKey("jdbc:savepointpgproxy://localhost/lealpoints_functional_test?protocolVersion=2"));
    }

    @Test
    public void testConnect() throws Exception {
        final String devDatabasePath = getDevDatabasePath();
        final String connectionUrl = getConnectionUrl(devDatabasePath);
        final Properties info = getInfoProperties();

        SavepointPgProxyDriver savepointPgProxyDriver = getSavepointPgProxyDriver();
        Connection connection = savepointPgProxyDriver.connect(connectionUrl, info);
        assertNotNull(connection);
        assertEquals("SavepointProxyConnectionImpl", connection.getClass().getSimpleName());

        ((SavepointProxyConnection) connection).rollbackTransactionForAutomationTest();
        assertFalse(connection.isClosed());

        connection.close();
        assertTrue(connection.isClosed());
    }

    @Test
    public void testGetConnectionShouldReturnNewConnectionWhenThereIsNoConnectionToReuse() throws Exception {
        final String devDatabasePath = getDevDatabasePath();
        final SavepointPgProxyDriver savepointPgProxyDriver = getSavepointPgProxyDriver();
        final Properties infoProperties = getInfoProperties();

        Connection clientDatabaseConnection = getConnection(devDatabasePath, savepointPgProxyDriver, infoProperties);
        assertNotNull(clientDatabaseConnection);
        assertEquals("SavepointProxyConnectionImpl", clientDatabaseConnection.getClass().getSimpleName());

        ((SavepointProxyConnection) clientDatabaseConnection).rollbackTransactionForAutomationTest();
        assertFalse(clientDatabaseConnection.isClosed());

        final String unitTestDatabasePath = getUnitTestDatabasePath();
        Connection unitTestDatabaseConnection = getConnection(unitTestDatabasePath, savepointPgProxyDriver, infoProperties);
        assertNotNull(unitTestDatabaseConnection);
        assertEquals("SavepointProxyConnectionImpl", clientDatabaseConnection.getClass().getSimpleName());

        assertNotSame(clientDatabaseConnection, unitTestDatabaseConnection);

        ((SavepointProxyConnection) unitTestDatabaseConnection).rollbackTransactionForAutomationTest();
        assertFalse(unitTestDatabaseConnection.isClosed());

        clientDatabaseConnection.close();
        assertTrue(clientDatabaseConnection.isClosed());

        unitTestDatabaseConnection.close();
        assertTrue(unitTestDatabaseConnection.isClosed());
    }

    @Test
    public void testGetConnectionShouldReturnReusableConnection() throws Exception {
        final String devDatabasePath = getDevDatabasePath();
        final SavepointPgProxyDriver savepointPgProxyDriver = getSavepointPgProxyDriver();
        final Properties infoProperties = getInfoProperties();

        Connection clientDatabaseConnection = getConnection(devDatabasePath, savepointPgProxyDriver, infoProperties);
        assertNotNull(clientDatabaseConnection);
        assertEquals("SavepointProxyConnectionImpl", clientDatabaseConnection.getClass().getSimpleName());

        Connection reusedClientDatabaseConnection = getConnection(devDatabasePath, savepointPgProxyDriver, infoProperties);
        assertNotNull(reusedClientDatabaseConnection);
        assertEquals("SavepointProxyConnectionImpl", clientDatabaseConnection.getClass().getSimpleName());
        assertNotEquals(clientDatabaseConnection, reusedClientDatabaseConnection);

        ((SavepointProxyConnection) reusedClientDatabaseConnection).beginTransactionForAutomationTest();

        Connection reusedClientDatabaseConnectionOnTransaction = getConnection(devDatabasePath, savepointPgProxyDriver, infoProperties);
        assertNotNull(reusedClientDatabaseConnectionOnTransaction);
        assertEquals("SavepointProxyConnectionImpl", clientDatabaseConnection.getClass().getSimpleName());
        assertEquals(reusedClientDatabaseConnection, reusedClientDatabaseConnectionOnTransaction);

        ((SavepointProxyConnection) reusedClientDatabaseConnection).rollbackTransactionForAutomationTest();

        assertFalse(reusedClientDatabaseConnection.isClosed());
        assertFalse(reusedClientDatabaseConnectionOnTransaction.isClosed());

        reusedClientDatabaseConnection.close();
        assertTrue(reusedClientDatabaseConnection.isClosed());
        assertTrue(reusedClientDatabaseConnectionOnTransaction.isClosed());
    }

    @Test
    public void testGetConnectionShouldReturnNewConnectionBecauseReusableIsClosed() throws Exception {
        final String devDatabasePath = getDevDatabasePath();
        final SavepointPgProxyDriver savepointPgProxyDriver = getSavepointPgProxyDriver();
        final Properties infoProperties = getInfoProperties();

        Connection clientDatabaseConnection = getConnection(devDatabasePath, savepointPgProxyDriver, infoProperties);
        assertNotNull(clientDatabaseConnection);
        assertEquals("SavepointProxyConnectionImpl", clientDatabaseConnection.getClass().getSimpleName());

        ((SavepointProxyConnection) clientDatabaseConnection).rollbackTransactionForAutomationTest();
        assertFalse(clientDatabaseConnection.isClosed());

        clientDatabaseConnection.close();
        assertTrue(clientDatabaseConnection.isClosed());

        Connection newClientDataBaseConnection = getConnection(devDatabasePath, savepointPgProxyDriver, infoProperties);
        assertNotNull(newClientDataBaseConnection);
        assertEquals("SavepointProxyConnectionImpl", clientDatabaseConnection.getClass().getSimpleName());
        assertNotEquals(clientDatabaseConnection, newClientDataBaseConnection);

        ((SavepointProxyConnection) newClientDataBaseConnection).rollbackTransactionForAutomationTest();
        assertFalse(newClientDataBaseConnection.isClosed());

        newClientDataBaseConnection.close();
        assertTrue(newClientDataBaseConnection.isClosed());
    }

    private String getDevDatabasePath() {
        return DbUtil.getDevDatabasePath();
    }

    private String getUnitTestDatabasePath() {
        return DbUtil.getUnitTestDatabasePath();
    }

    private Connection getConnection(String clientDatabasePath, SavepointPgProxyDriver savepointPgProxyDriver, Properties infoProperties)
        throws SQLException {
        return savepointPgProxyDriver.getConnection(getConnectionUrl(clientDatabasePath), infoProperties, getUrlForWrappedDriver());
    }

    private String getConnectionUrl(String fullDatabasePath) {
        return getDriverUrl() + fullDatabasePath;
    }

    private String getDriverUrl() {
        return "jdbc:savepointpgproxy://";
    }

    private Properties getInfoProperties() {
        Properties info = new Properties();
        info.setProperty("user", "postgres");
        info.setProperty("password", "1234");
        return info;
    }

    private String getUrlForWrappedDriver() {
        return "jdbc:postgresql://localhost/lealpoints";
    }
}