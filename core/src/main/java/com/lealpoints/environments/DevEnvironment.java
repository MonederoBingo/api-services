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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof DevEnvironment)) {
            return false;
        }
        DevEnvironment that = (DevEnvironment) obj;
        return getDatabasePath().equals(that.getDatabasePath());
    }

    @Override
    public int hashCode() {
        return getDatabasePath().hashCode();
    }
}
