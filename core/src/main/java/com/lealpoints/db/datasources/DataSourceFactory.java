package com.lealpoints.db.datasources;

import javax.sql.DataSource;
import com.lealpoints.environments.Environment;

public interface DataSourceFactory {
    DataSource getDataSource(Environment environment);
}
