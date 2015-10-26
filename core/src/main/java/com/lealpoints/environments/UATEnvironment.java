package com.lealpoints.environments;

import com.lealpoints.common.PropertyManager;

public class UATEnvironment extends Environment {

    @Override
    public String getDatabasePath() {
        return PropertyManager.getProperty("db_driver") + PropertyManager.getProperty("uat.db_url");
    }

    @Override
    public String getDatabaseDriverClass() {
        return PropertyManager.getProperty("db_driver_class");
    }

    @Override
    public String getDatabaseUsername() {
        return PropertyManager.getProperty("uat.db_user");
    }

    @Override
    public String getDatabasePassword() {
        return PropertyManager.getProperty("uat.db_password");
    }

    @Override
    public String getImageDir() {
        return System.getenv("OPENSHIFT_DATA_DIR") + "images/uat/";
    }

    @Override
    public String getClientUrl() {
        return PropertyManager.getProperty("uat.client_url");
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof UATEnvironment)) {
            return false;
        }
        UATEnvironment that = (UATEnvironment) obj;
        return getDatabasePath().equals(that.getDatabasePath());
    }

    @Override
    public int hashCode() {
        return getDatabasePath().hashCode();
    }
}
