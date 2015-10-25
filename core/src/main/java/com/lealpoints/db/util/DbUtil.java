package com.lealpoints.db.util;

import com.lealpoints.common.PropertyManager;

public class DbUtil {

    public static String getUnitTestDatabasePath() {
        return PropertyManager.getProperties().getProperty("unit_test.db_url");
    }

    public static String getDevDatabasePath() {
        return PropertyManager.getProperties().getProperty("dev.db_url");
    }
}
