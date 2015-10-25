package com.lealpoints.environments;

import com.lealpoints.common.PropertyManager;

public class DevEnvironment extends Environment {

    @Override
    public String getDatabasePath() {
        return PropertyManager.getProperty("db_driver") + PropertyManager.getProperty("dev.db_url");
    }

    @Override
    public String getDatabaseDriverClass() {
        return PropertyManager.getProperty("db_driver_class");
    }

    @Override
    public String getDatabaseUsername() {
        return PropertyManager.getProperty("dev.db_user");
    }

    @Override
    public String getDatabasePassword() {
        return PropertyManager.getProperty("dev.db_password");
    }

    @Override
    public String getImageDir() {
        return PropertyManager.getProperty("dev.images_dir");
    }

    @Override
    public String getClientUrl() {
        return PropertyManager.getProperty("dev.client_url");
    }
}
