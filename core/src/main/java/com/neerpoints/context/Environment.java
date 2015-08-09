package com.neerpoints.context;

import com.neerpoints.db.DatabaseManager;
import com.neerpoints.db.DevelopmentDatabaseManager;
import com.neerpoints.db.FunctionalTestDatabaseManager;
import com.neerpoints.db.ProductionDatabaseManager;
import com.neerpoints.db.UATDatabaseManager;

public enum Environment {
    DEV(new DevelopmentDatabaseManager(), "src/main/webapp/images/dev/", "http://localhost:8080/#/"),
    DEV_TEST(new FunctionalTestDatabaseManager(), "src/main/webapp/images/test/", "http://test.localhost:8080/#/"),
    UAT(new UATDatabaseManager(), System.getenv("OPENSHIFT_DATA_DIR") + "images/uat/", "http://test.neerpoints.com/#/"),
    PROD(new ProductionDatabaseManager(), System.getenv("OPENSHIFT_DATA_DIR") + "images/prod/", "http://www.neerpoints.com/#/");

    private DatabaseManager _databaseManager;
    private String _imageDir;
    private String _clientUrl;

    private Environment(DatabaseManager databaseManager, String imageDir, String clientUrl) {
        _databaseManager = databaseManager;
        _imageDir = imageDir;
        _clientUrl = clientUrl;
    }

    public DatabaseManager getDatabaseManager() {
        return _databaseManager;
    }

    public String getImageDir() {
        return _imageDir;
    }

    public String getClientUrl() {
        return _clientUrl;
    }
}
