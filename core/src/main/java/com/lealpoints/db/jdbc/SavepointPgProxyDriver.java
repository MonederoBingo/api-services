package com.lealpoints.db.jdbc;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import org.apache.commons.collections15.keyvalue.MultiKey;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;

public class SavepointPgProxyDriver extends org.postgresql.Driver {
    private static final org.apache.logging.log4j.Logger _logger = LogManager.getLogger(SavepointPgProxyDriver.class.getName());

    private static final String DRIVER_URL_PROTOCOL = "jdbc:savepointpgproxy:";
    private static final Pattern DRIVER_URL_REGEX_PATTERN = Pattern.compile("^" + DRIVER_URL_PROTOCOL + ".*");
    private static final String WRAPPED_DRIVER_URL_PROTOCOL = "jdbc:postgresql:";
    private static final String WRAPPED_DRIVER_CLASS_NAME = "org.postgresql.Driver";
    private static final Pattern DRIVER_URL_PROTOCOL_PATTERN = Pattern.compile(DRIVER_URL_PROTOCOL, Pattern.LITERAL);
    private static final Pattern URL_CONNECTION_KEY_REGEX_PATTERN = Pattern.compile("\\?.*$");
    private final Map<MultiKey<Object>, Queue<SavepointProxyConnection>> _sharedConnectionMap = new ConcurrentHashMap<>();
    private Driver _wrappedDriver;
    private boolean _isProxyConnectionActive = false;

    private SavepointPgProxyDriver() {
    }

    static {
        _logger.info("Using " + getClassSimpleName() + " to wrap Postgres JDBC driver");
        SavepointPgProxyDriver driver = new SavepointPgProxyDriver();
        driver.setWrappedDriver(findWrappedDriver());
        try {
            DriverManager.registerDriver(driver);
        } catch (SQLException e) {
            logAndThrowException(e.getMessage());
        }
    }

    private static String getClassSimpleName() {
        return SavepointPgProxyDriver.class.getSimpleName();
    }

    private static Driver findWrappedDriver() {
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            if (driver.getClass().getName().equals(WRAPPED_DRIVER_CLASS_NAME)) {
                return driver;
            }
        }
        logAndThrowException("Could not find PostgreSQL JDBC Driver");
        return null;
    }

    private static Driver logAndThrowException(String errorMessage) {
        _logger.error(errorMessage);
        throw new RuntimeException(errorMessage);
    }

    @Override
    public boolean acceptsURL(String url) {
        return StringUtils.isNotBlank(url) && DRIVER_URL_REGEX_PATTERN.matcher(url).matches();
    }

    String getUrlForConnectionKey(String url) {
        //We remove all extra-params in connection url since we only need protocol, host and database name to reuse connections from url.
        return URL_CONNECTION_KEY_REGEX_PATTERN.matcher(url).replaceFirst("");
    }

    private void setWrappedDriver(Driver driver) {
        _wrappedDriver = driver;
    }

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        if (StringUtils.isNotBlank(url) && DRIVER_URL_REGEX_PATTERN.matcher(url).matches()) {
            String urlForWrappedDriver = DRIVER_URL_PROTOCOL_PATTERN.matcher(url).replaceAll(WRAPPED_DRIVER_URL_PROTOCOL);
            return getConnection(url, info, urlForWrappedDriver);
        }
        logAndThrowException("Could not connect to wrapped driver (PostgreSQL JDBC Driver)");
        return null;
    }

    synchronized Connection getConnection(String url, Properties info, String urlForWrappedDriver) throws SQLException {
        String urlForConnectionKey = getUrlForConnectionKey(url);
        MultiKey<Object> connectionKey = new MultiKey<>(new Object[]{urlForConnectionKey, info});

        SavepointProxyConnection savepointProxyConnection = null;

        Queue<SavepointProxyConnection> savepointProxyConnectionList = _sharedConnectionMap.get(connectionKey);

        if (savepointProxyConnectionList == null) {
            savepointProxyConnectionList = new LinkedList<>();
            savepointProxyConnection = createNewConnection(info, urlForWrappedDriver);
            savepointProxyConnectionList.add(savepointProxyConnection);
            _sharedConnectionMap.put(connectionKey, savepointProxyConnectionList);
        } else {
            cleanupConnections(savepointProxyConnectionList, urlForWrappedDriver);
            for (SavepointProxyConnection savepointProxyConnectionFromList : savepointProxyConnectionList) {
                if (savepointProxyConnectionFromList.isProxyConnectionActive()) {
                    savepointProxyConnection = savepointProxyConnectionFromList;
                    break;
                }
            }
        }

        if (savepointProxyConnection == null || savepointProxyConnection.isClosed()) {
            savepointProxyConnection = createNewConnection(info, urlForWrappedDriver);
            savepointProxyConnectionList.add(savepointProxyConnection);
            _logger.info(String.format("==== SavepointProxyConnection CREATED on %s. Number of connections %s", urlForWrappedDriver,
                savepointProxyConnectionList.size()));
            _sharedConnectionMap.put(connectionKey, savepointProxyConnectionList);
        }

        return savepointProxyConnection;
    }

    private SavepointProxyConnection createNewConnectionAndAddToList(Properties info, String urlForWrappedDriver, MultiKey<Object> connectionKey,
        Queue<SavepointProxyConnection> savepointProxyConnectionList) throws SQLException {
        SavepointProxyConnection savepointProxyConnection;
        savepointProxyConnection = createNewConnection(info, urlForWrappedDriver);
        savepointProxyConnectionList.add(savepointProxyConnection);
        _sharedConnectionMap.put(connectionKey, savepointProxyConnectionList);
        return savepointProxyConnection;
    }

    private String removeExtraParameterInConnectionUrl(String url) {
        return URL_CONNECTION_KEY_REGEX_PATTERN.matcher(url).replaceFirst("");
    }

    private SavepointProxyConnection createNewConnection(Properties info, String urlForWrappedDriver) throws SQLException {
        Connection wrappedConnection = _wrappedDriver.connect(urlForWrappedDriver, info);
        SavepointProxyConnection connection = new SavepointProxyConnectionImpl(wrappedConnection, this);
        connection.setConnectionUrl(urlForWrappedDriver);
        return connection;
    }

    private void cleanupConnections(Queue<SavepointProxyConnection> savepointProxyConnectionList, String urlForWrappedDriver) throws SQLException {
        if (savepointProxyConnectionList != null) {
            List<SavepointProxyConnection> closedConnections = new ArrayList<>();

            for (SavepointProxyConnection savepointProxyConnection : savepointProxyConnectionList) {
                if (canCloseConnection(savepointProxyConnection)) {
                    savepointProxyConnection.close();
                }
            }

            for (SavepointProxyConnection savepointProxyConnection : savepointProxyConnectionList) {
                if (savepointProxyConnection.isClosed()) {
                    closedConnections.add(savepointProxyConnection);
                }
            }

            for (SavepointProxyConnection closedConnection : closedConnections) {
                savepointProxyConnectionList.remove(closedConnection);
            }
            if (savepointProxyConnectionList.size() > 3) {
                _logger.warn(String
                    .format("==== SavepointProxyConnection open connections %s for %s", savepointProxyConnectionList.size(), urlForWrappedDriver));
            }
        }
    }

    private boolean canCloseConnection(SavepointProxyConnection savepointProxyConnection) throws SQLException {
        return !savepointProxyConnection.isClosed() && isProxyConnectionActive() && !savepointProxyConnection.isProxyConnectionActive() &&
            savepointProxyConnection.getAutoCommit();
    }

    public boolean isProxyConnectionActive() {
        return _isProxyConnectionActive;
    }

    public void setProxyConnectionActive(boolean isProxyConnectionActive) {
        _isProxyConnectionActive = isProxyConnectionActive;
    }
}
