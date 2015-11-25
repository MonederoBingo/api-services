package com.lealpoints.db.datasources;

import com.lealpoints.db.util.concurrent.Computable;
import com.lealpoints.db.util.concurrent.Memoizer;
import com.lealpoints.environments.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import static com.lealpoints.db.util.DbUtil.createDataSource;

@Component
public class DataSourceFactoryImpl implements DataSourceFactory {
    private static final Logger _logger = LogManager.getLogger(DataSource.class.getName());

    private final Computable<Environment, DataSource> _computable = new Computable<Environment, DataSource>() {
        @Override
        public DataSource compute(Environment arg) throws InterruptedException {
            return createDataSource(arg);
        }
    };

    private final Computable<Environment, DataSource> _dataSources = new Memoizer<>(_computable);

    @Override
    public DataSource getDataSource(Environment environment) {
        try {
            final DataSource dataSource = _dataSources.compute(environment);
            if (dataSource == null) {
                throw new RuntimeException("DataSource cannot be null!");
            }
            return dataSource;
        } catch (InterruptedException e) {
            _logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
