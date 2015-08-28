package com.lealpoints.context;

import javax.sql.DataSource;
import com.lealpoints.db.DataSourceFactory;
import org.springframework.stereotype.Component;

@Component
public enum Environment {
    DEV(DataSourceFactory.getDevDataSource(), "src/main/webapp/images/dev/", "http://localhost:8080/#/"),
    FUNCTIONAL_TEST(DataSourceFactory.getFunctionalTestDataSource(), "src/main/webapp/images/test/", "http://test.localhost:8080/#/"),
    UAT(DataSourceFactory.getUATDataSource(), System.getenv("OPENSHIFT_DATA_DIR") + "images/uat/", "http://test.lealpoints.com/#/"),
    PROD(DataSourceFactory.getProdDataSource(), System.getenv("OPENSHIFT_DATA_DIR") + "images/prod/", "http://www.lealpoints.com/#/");

    private DataSource _dataSource;
    private String _imageDir;
    private String _clientUrl;

    private Environment(DataSource dataSource, String imageDir, String clientUrl) {
        _dataSource = dataSource;
        _imageDir = imageDir;
        _clientUrl = clientUrl;
    }

    public DataSource getDataSource() {
        return _dataSource;
    }

    public String getImageDir() {
        return _imageDir;
    }

    public String getClientUrl() {
        return _clientUrl;
    }
}
