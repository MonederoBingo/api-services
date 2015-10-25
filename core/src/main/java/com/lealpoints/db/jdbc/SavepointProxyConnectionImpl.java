package com.lealpoints.db.jdbc;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.Executor;
import org.apache.logging.log4j.LogManager;

public class SavepointProxyConnectionImpl implements SavepointProxyConnection {

    private static final org.apache.logging.log4j.Logger _logger = LogManager.getLogger(SavepointProxyConnectionImpl.class.getName());
    private final Connection _wrappedConnection;
    private final SavepointPgProxyDriver _driver;
    private boolean _isProxyConnectionActive = false;
    private String _connectionUrl;
    private Savepoint _lastSavepoint;
    private String _automationTestName;

    public SavepointProxyConnectionImpl(Connection wrappedConnection, SavepointPgProxyDriver savepointPgProxyDriver) {
        _wrappedConnection = wrappedConnection;
        _driver = savepointPgProxyDriver;
    }

    @Override
    public Statement createStatement() throws SQLException {
        return _wrappedConnection.createStatement();
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return _wrappedConnection.prepareStatement(sql);
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        return _wrappedConnection.prepareCall(sql);
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        return _wrappedConnection.nativeSQL(sql);
    }

    @Override
    public synchronized boolean getAutoCommit() throws SQLException {
        return !isProxyConnectionActive() && _wrappedConnection.getAutoCommit();
    }

    @Override
    public synchronized void setAutoCommit(boolean autoCommit) throws SQLException {
        if (isProxyConnectionActive()) {
            if (!autoCommit) {
                releaseLastSavepoint();
                setNewSavepoint();
            }
        } else {
            if (isClosed()) {
                _logger.warn(String.format("Attempt of setting autoCommit to %s . Connection is already closed.", autoCommit));
            } else {
                if (getAutoCommit() != autoCommit) {
                    _wrappedConnection.setAutoCommit(autoCommit);
                }
            }
        }
    }

    @Override
    public synchronized void commit() throws SQLException {
        if (isProxyConnectionActive()) {
            releaseLastSavepoint();
            setNewSavepoint();
        } else {
            _wrappedConnection.commit();
        }
    }

    @Override
    public synchronized void rollback() throws SQLException {
        if (isProxyConnectionActive()) {
            rollbackLastSavepoint();
            setNewSavepoint();
        } else {
            if (isClosed()) {
                _logger.warn("Attempt of rollback. Connection is already closed.");
            } else {
                if (getAutoCommit()) {
                    _logger.warn(String.format("Attempt of rollback. Auto-commit is true - - connection URL: %s", _connectionUrl));
                } else {
                    _wrappedConnection.rollback();
                }
            }
        }
    }

    @Override
    public synchronized void close() throws SQLException {
        if (isClosed()) {
            StringBuilder closedConnectionMessage = new StringBuilder(
                String.format("Attempt of close. Connection is already closed. Autocommit is %s.", _wrappedConnection.getAutoCommit()));
            if (isProxyConnectionActive()) {
                closedConnectionMessage.append(" And SavepointProxyConnection is still active.");
            }
            closedConnectionMessage.append(String.format(" On %s", _connectionUrl));
            _logger.warn(closedConnectionMessage.toString());
        } else {
            if (!isProxyConnectionActive()) {
                if (_wrappedConnection.getAutoCommit()) {
                    _wrappedConnection.close();
                    _logger.info(String.format("==== SavepointProxyConnection CLOSED on %s", _connectionUrl));
                } else {
                    _logger.warn(String.format("Attempt to close connection when auto-commit is false - connection URL: %s", _connectionUrl));
                }
            }
        }
    }

    @Override
    public synchronized boolean isClosed() throws SQLException {
        return _wrappedConnection.isClosed();
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return _wrappedConnection.getMetaData();
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return _wrappedConnection.isReadOnly();
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        _wrappedConnection.setReadOnly(readOnly);
    }

    @Override
    public String getCatalog() throws SQLException {
        return _wrappedConnection.getCatalog();
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        _wrappedConnection.setCatalog(catalog);
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        return _wrappedConnection.getTransactionIsolation();
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        _wrappedConnection.setTransactionIsolation(level);
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return _wrappedConnection.getWarnings();
    }

    @Override
    public void clearWarnings() throws SQLException {
        _wrappedConnection.clearWarnings();
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return _wrappedConnection.createStatement(resultSetType, resultSetConcurrency);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return _wrappedConnection.prepareStatement(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return _wrappedConnection.prepareCall(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return _wrappedConnection.getTypeMap();
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        _wrappedConnection.setTypeMap(map);
    }

    @Override
    public int getHoldability() throws SQLException {
        return _wrappedConnection.getHoldability();
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
        _wrappedConnection.setHoldability(holdability);
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        return null;
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        return _wrappedConnection.setSavepoint(name);
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        _wrappedConnection.rollback(savepoint);
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        _wrappedConnection.releaseSavepoint(savepoint);
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return _wrappedConnection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return _wrappedConnection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return _wrappedConnection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return _wrappedConnection.prepareStatement(sql, autoGeneratedKeys);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return _wrappedConnection.prepareStatement(sql, columnIndexes);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        return _wrappedConnection.prepareStatement(sql, columnNames);
    }

    @Override
    public Clob createClob() throws SQLException {
        return _wrappedConnection.createClob();
    }

    @Override
    public Blob createBlob() throws SQLException {
        return _wrappedConnection.createBlob();
    }

    @Override
    public NClob createNClob() throws SQLException {
        return _wrappedConnection.createNClob();
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        return _wrappedConnection.createSQLXML();
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        return _wrappedConnection.isValid(timeout);
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        _wrappedConnection.setClientInfo(name, value);
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        return _wrappedConnection.getClientInfo(name);
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return _wrappedConnection.getClientInfo();
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        _wrappedConnection.setClientInfo(properties);
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return _wrappedConnection.createArrayOf(typeName, elements);
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return _wrappedConnection.createStruct(typeName, attributes);
    }

    @Override
    public String getSchema() throws SQLException {
        return _wrappedConnection.getSchema();
    }

    @Override
    public void setSchema(String schema) throws SQLException {
        _wrappedConnection.setSchema(schema);
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        _wrappedConnection.abort(executor);
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        _wrappedConnection.setNetworkTimeout(executor, milliseconds);
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        return _wrappedConnection.getNetworkTimeout();
    }

    @Override
    public <T> T unwrap(Class<T> tClass) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> aClass) throws SQLException {
        return false;
    }

    @Override
    public synchronized boolean isProxyConnectionActive() {
        return _isProxyConnectionActive;
    }

    // Attribute should not be modified directly but through public methods beginTransactionForAutomationTest or rollbackTransactionForAutomationTest
    private synchronized void setProxyConnectionActive(boolean isProxyConnectionActive) {
        _isProxyConnectionActive = isProxyConnectionActive;
        _driver.setProxyConnectionActive(isProxyConnectionActive);
    }

    private Savepoint getLastSavepoint() {
        return _lastSavepoint;
    }

    private void setLastSavepoint(Savepoint lastSavepoint) {
        if (_lastSavepoint != null && lastSavepoint != null) {
            _logger.info("Setting savepoint when _lastSavepoint is not null");
        }
        _lastSavepoint = lastSavepoint;
    }

    private synchronized void setNewSavepoint() throws SQLException {
        if (getLastSavepoint() != null) {
            _logger.info("Setting savepoint when _lastSavepoint is not null");
        }
        String savepointName = UUID.randomUUID().toString();
        Savepoint savepoint = _wrappedConnection.setSavepoint(savepointName);
        setLastSavepoint(savepoint);
    }

    @Override
    public synchronized void beginTransactionForAutomationTest(String automationTestName) throws SQLException {
        if (!isProxyConnectionActive()) {
            _wrappedConnection.setAutoCommit(false);
            setNewSavepoint();
            setProxyConnectionActive(true);
            _automationTestName = automationTestName;
            _logger
                .info(String.format("==== SavepointProxyConnection TRANSACTION START on %s for test %s", getMetaData().getURL(), automationTestName));
        }
    }

    @Override
    public synchronized void rollbackTransactionForAutomationTest() throws SQLException {
        if (isProxyConnectionActive()) {
            _wrappedConnection.rollback();
            _wrappedConnection.setAutoCommit(true);
            setProxyConnectionActive(false);
            _logger.info(String.format("==== SavepointProxyConnection TRANSACTION END on %s for test %s", _connectionUrl, _automationTestName));
        }
    }

    private synchronized void releaseLastSavepoint() throws SQLException {
        Savepoint lastSavepoint = getLastSavepoint();
        if (lastSavepoint != null) {
            _wrappedConnection.releaseSavepoint(lastSavepoint);
            setLastSavepoint(null);
        }
    }

    private synchronized void rollbackLastSavepoint() throws SQLException {
        Savepoint lastSavepoint = getLastSavepoint();
        if (lastSavepoint != null) {
            _wrappedConnection.rollback(lastSavepoint);
            setLastSavepoint(null);
        }
    }

    @Override
    public String getConnectionUrl() {
        return _connectionUrl;
    }

    @Override
    public void setConnectionUrl(String connectionUrl) {
        _connectionUrl = connectionUrl;
    }
}
