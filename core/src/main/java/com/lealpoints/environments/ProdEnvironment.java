package com.lealpoints.environments;

import com.lealpoints.common.PropertyManager;

public class ProdEnvironment extends Environment {

    @Override
    public String getDatabasePath() {
        return PropertyManager.getProperty("db_savepoint_driver") + PropertyManager.getProperty("prod.db_url");
    }

    @Override
    public String getDatabaseDriverClass() {
        return PropertyManager.getProperty("db_savepoint_driver_class");
    }

    @Override
    public String getDatabaseUsername() {
        return PropertyManager.getProperty("prod.db_user");
    }

    @Override
    public String getDatabasePassword() {
        return PropertyManager.getProperty("prod.db_password");
    }

    @Override
    public String getImageDir() {
        return System.getenv("OPENSHIFT_DATA_DIR") + "images/prod/";
    }

    @Override
    public String getClientUrl() {
        return PropertyManager.getProperty("prod.client_url");
    }
}
