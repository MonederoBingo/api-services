package com.lealpoints.db.queryagent;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import com.lealpoints.db.jdbc.SavepointProxyConnection;
import com.lealpoints.db.util.DbBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class QueryAgent {
    private static final Logger _logger = LogManager.getLogger(QueryAgent.class.getName());

    private final transient DataSource _dataSource;
    private transient Connection _connection = null;
    private boolean _isInTransaction;

    public QueryAgent(DataSource dataSource) {
        _dataSource = dataSource;
    }

    /**
     * Creates a new connection to a postgres database
     *
     * @return The established connection.
     * @throws Exception
     */
    public Connection getConnection() {
        if (_connection == null) {
            try {
                _connection = _dataSource.getConnection();
            } catch (SQLException e) {
                _logger.error(e.getMessage());
                throw new RuntimeException(e.getMessage(), e);
            }
        }
        return _connection;
    }

    /**
     * Executes an insert statement.
     *
     * @param sql Insert sql query to be executed.
     * @param id  Column name of the generated key to be returned from the query execution.
     * @return The generated key of the id column returned from the query execution.
     * @throws Exception
     */
    public long executeInsert(String sql, String id) throws Exception {
        Connection connection = getConnection();
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql, Statement.RETURN_GENERATED_KEYS);
            ResultSet resultSet = statement.getGeneratedKeys();
            resultSet.next();
            return resultSet.getLong(id);
        } finally {
            releaseConnectionIfPossible();
        }
    }

    /**
     * Executes and update statement.
     *
     * @param sql Update statement to be executed.
     * @return The number of rows returned from the query.
     * @throws Exception
     */
    public int executeUpdate(String sql) throws Exception {
        Connection connection = getConnection();
        try (Statement statement = connection.createStatement()) {
            return statement.executeUpdate(sql);
        } finally {
            releaseConnectionIfPossible();
        }
    }

    /**
     * Executes a Select statement in the database and returns multiple rows.
     *
     * @param builder Helper object that contains placeholders and build method
     * @param <T>     Type of object to be returned as list and built by DbBuilder
     * @return The list of type T built from the select statement execution
     * @throws SQLException
     */
    public synchronized <T> List<T> selectList(DbBuilder<T> builder) throws Exception {
        Connection connection = getConnection();
        try (PreparedStatement statement = connection.prepareStatement(builder.sql())) {
            setValues(statement, builder.values());

            try (ResultSet resultSet = statement.executeQuery()) {
                List<T> results = new ArrayList<>();
                while (resultSet.next()) {
                    results.add(builder.build(resultSet));
                }
                return results;
            }
        } finally {
            releaseConnectionIfPossible();
        }
    }

    /**
     * Executes a Select statement in the database and returns only one object
     *
     * @param builder Helper object that contains placeholders and build method
     * @param <T>     Type of object to be returned and built by DbBuilder
     * @return The type T built from the select statement execution
     * @throws SQLException
     */
    public synchronized <T> T selectObject(DbBuilder<T> builder) throws Exception {

        try (PreparedStatement statement = getConnection().prepareStatement(builder.sql())) {
            setValues(statement, builder.values());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }
                return builder.build(resultSet);
            }
        } finally {
            releaseConnectionIfPossible();
        }
    }

    public synchronized void beginTransaction() throws Exception {
        getConnection().setAutoCommit(false);
        _isInTransaction = true;
    }

    public synchronized void commitTransaction() throws SQLException {
        try {
            _connection.commit();
            _connection.setAutoCommit(true);
            _isInTransaction = false;
        } finally {
            releaseConnectionIfPossible();
        }
    }

    public synchronized void rollbackTransaction() throws Exception {
        if (_connection != null) {
            try {
                getConnection().rollback();
                getConnection().setAutoCommit(true);
                _isInTransaction = false;
            } finally {
                releaseConnectionIfPossible();
            }
        }
    }

    public boolean isInTransaction() {
        return _isInTransaction;
    }

    protected synchronized void releaseConnectionIfPossible() {
        if (!isInTransaction() && _connection != null) {
            try {
                _connection.close();
            } catch (SQLException e) {
                throw new RuntimeException("Error releasing connection");
            }
            _connection = null;
        }
    }

    private void setValues(PreparedStatement statement, Object... values) throws SQLException {
        if (values == null) {
            return;
        }
        for (int i = 0; i < values.length; i++) {
            Object obj = values[i];
            if (obj == null) {
                statement.setNull(i + 1, Types.NULL);
            } else if (obj instanceof String) {
                statement.setString(i + 1, (String) obj);
            } else if (obj instanceof Integer) {
                statement.setInt(i + 1, (Integer) obj);
            } else if (obj instanceof Boolean) {
                statement.setBoolean(i + 1, (Boolean) obj);
            } else if (obj instanceof Double) {
                statement.setDouble(i + 1, (Double) obj);
            } else if (obj instanceof Long) {
                statement.setLong(i + 1, (Long) obj);
            } else if (obj instanceof String[]) {
                statement.setArray(i + 1, statement.getConnection().createArrayOf("varchar", (String[]) obj));
            } else {
                throw new RuntimeException("Unsupported SQL type for object : " + obj);
            }
        }

    }

    @Override
    protected void finalize() throws Throwable {
        if (_connection != null) {
            _connection.close();
            _connection = null;
        }
        super.finalize();
    }

    public synchronized void beginTransactionForFunctionalTest() {
        SavepointProxyConnection connection = (SavepointProxyConnection) getConnection();
        try {
            connection.beginTransactionForAutomationTest();
            _connection = connection;
        } catch (SQLException e) {
            _logger.error(e.getMessage());
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public synchronized void rollbackTransactionForFunctionalTest() {
        SavepointProxyConnection connection = (SavepointProxyConnection) getConnection();
        try {
            connection.rollbackTransactionForAutomationTest();
            _connection = null;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
