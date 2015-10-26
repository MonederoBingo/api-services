package com.lealpoints.db.datasources;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;
import com.lealpoints.environments.EnvironmentFactory;
import com.lealpoints.environments.EnvironmentFactoryImpl;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DataSourceFactoryImplTest {

    @Test
    public void testCreateDataSource() throws SQLException {
        DataSourceFactoryImpl dataSourceFactory = new DataSourceFactoryImpl();
        EnvironmentFactory environmentFactory = new EnvironmentFactoryImpl();
        Connection connection = null;
        try {
            DataSource dataSource = dataSourceFactory.getDataSource(environmentFactory.getUnitTestEnvironment());
            connection = dataSource.getConnection();
            assertEquals("postgres", connection.getMetaData().getUserName());
        } finally {
            closeConnection(connection);
        }
    }

    @Test
    public void testCreateDataSourceWithTwoThreads() throws SQLException, InterruptedException {
        int count = 1000;
        while (count-- > 0) {
            final DataSourceFactoryImpl dataSourceFactory = new DataSourceFactoryImpl();
            final EnvironmentFactory environmentFactory = new EnvironmentFactoryImpl();
            final DataSource[] dataSource = new DataSource[2];
            final CountDownLatch startSignal = new CountDownLatch(1);
            final CountDownLatch doneSignal = new CountDownLatch(2);
            Thread firstThread = new Thread() {
                @Override
                public void run() {
                    awaitOnStart(startSignal);
                    dataSource[0] = dataSourceFactory.getDataSource(environmentFactory.getUnitTestEnvironment());
                    markAsDone(doneSignal);
                }
            };

            Thread secondThread = new Thread() {
                @Override
                public void run() {
                    awaitOnStart(startSignal);
                    dataSource[1] = dataSourceFactory.getDataSource(environmentFactory.getUnitTestEnvironment());
                    markAsDone(doneSignal);
                }
            };

            firstThread.start();
            secondThread.start();
            assertNull(dataSource[0]);
            assertNull(dataSource[1]);
            markAsDone(startSignal);
            doneSignal.await();
            assertEquals(dataSource[0], dataSource[1]);
        }
    }

    private void markAsDone(CountDownLatch doneSignal) {
        doneSignal.countDown();
    }

    private void awaitOnStart(CountDownLatch startSignal) {
        try {
            startSignal.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCreateDataSourceForProxiedConnection() throws SQLException {
        DataSourceFactoryImpl dataSourceFactory = new DataSourceFactoryImpl();
        EnvironmentFactory environmentFactory = new EnvironmentFactoryImpl();
        Connection connection = null;
        try {
            DataSource dataSource = dataSourceFactory.getDataSource(environmentFactory.getFunctionalTestEnvironment());
            connection = dataSource.getConnection();
            assertEquals("postgres", connection.getMetaData().getUserName());
            assertEquals("SavepointProxyConnectionImpl", connection.getClass().getSimpleName());
        } finally {
            closeConnection(connection);
        }
    }

    private void closeConnection(Connection connection) throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }
}