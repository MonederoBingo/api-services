package com.lealpoints.environments;

import com.lealpoints.common.PropertyManager;

public class UnitTestEnvironment extends Environment {

    @Override
    public String getDatabasePath() {
        return PropertyManager.getProperty("db_driver") + PropertyManager.getProperty("unit_test.db_url");
    }

    @Override
    public String getDatabaseDriverClass() {
        return PropertyManager.getProperty("db_driver_class");
    }

    @Override
    public String getDatabaseUsername() {
        return PropertyManager.getProperty("unit_test.db_user");
    }

    @Override
    public String getDatabasePassword() {
        return PropertyManager.getProperty("unit_test.db_password");
    }

    @Override
    public String getImageDir() {
        return PropertyManager.getProperty("unit_test.images_dir");
    }

    @Override
    public String getClientUrl() {
        return PropertyManager.getProperty("unit_test.client_url");
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof UnitTestEnvironment)) {
            return false;
        }
        UnitTestEnvironment that = (UnitTestEnvironment) obj;
        return getDatabasePath().equals(that.getDatabasePath());
    }

    @Override
    public int hashCode() {
        return getDatabasePath().hashCode();
    }
}
