package com.lealpoints.db.datasources;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.lealpoints.environments.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

@Component
public class DataSourceFactoryImpl implements DataSourceFactory {
    private static final Logger _logger = LogManager.getLogger(DataSource.class.getName());
    private final Map<String, DataSource> _dataSources = new ConcurrentHashMap<>();

    @Override
    public DataSource getDataSource(Environment environment) {
        final String key = environment.getDatabasePath();
        DataSource dataSource = _dataSources.get(key);
        if (dataSource == null) {
            dataSource = createDataSource(environment);
            _dataSources.putIfAbsent(key, dataSource);
            if (_logger.isInfoEnabled()) {
                _logger.info("Adding new datasource, current data source keys = " + _dataSources.keySet());
            }
        }
        return dataSource;
    }

    private DataSource createDataSource(Environment environment) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(environment.getDatabaseDriverClass());
        dataSource.setUrl(environment.getDatabasePath());
        dataSource.setUsername(environment.getDatabaseUsername());
        dataSource.setPassword(environment.getDatabasePassword());
        return dataSource;
    }
}
