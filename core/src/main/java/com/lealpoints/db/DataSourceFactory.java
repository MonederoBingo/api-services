package com.lealpoints.db;

import javax.sql.DataSource;
import java.util.Properties;
import com.lealpoints.common.PropertyManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class DataSourceFactory {

    private static DataSource _devDataSource = null;
    private static DataSource _functionalTestDataSource = null;
    private static DataSource _unitTestDataSource = null;
    private static DataSource _uatDataSource = null;
    private static DataSource _prodDataSource = null;

    public static DataSource getDevDataSource() {
        if (_devDataSource == null) {
            _devDataSource = createDataSource("dev");
        }
        return _devDataSource;
    }

    public static DataSource getFunctionalTestDataSource() {
        if (_functionalTestDataSource == null) {
            _functionalTestDataSource = createDataSource("functional_test");
        }
        return _functionalTestDataSource;
    }

    public static DataSource getUnitTestDataSource() {
        if (_unitTestDataSource == null) {
            _unitTestDataSource = createDataSource("unit_test");
        }
        return _unitTestDataSource;
    }

    public static DataSource getUATDataSource() {
        if (_uatDataSource == null) {
            _uatDataSource = createDataSource("uat");
        }
        return _uatDataSource;
    }

    public static DataSource getProdDataSource() {
        if (_prodDataSource == null) {
            _prodDataSource = createDataSource("prod");
        }
        return _prodDataSource;
    }

    private static DataSource createDataSource(String key) {
        final Properties properties = PropertyManager.getProperties();
        final String url = properties.getProperty(key + ".db_url");
        final String driver = properties.getProperty(key + ".db_driver");
        final String username = properties.getProperty(key + ".db_user");
        final String password = properties.getProperty(key + ".db_password");
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driver);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }
}
