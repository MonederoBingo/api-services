package com.lealpoints.migrations;

import com.lealpoints.db.datasources.DataSourceFactoryImpl;
import com.lealpoints.db.util.DbUtil;
import com.lealpoints.environments.DevEnvironment;
import com.lealpoints.environments.FunctionalTestEnvironment;
import com.lealpoints.environments.UnitTestEnvironment;
import com.lealpoints.migrations.util.MigrationUtil;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreateDb {
    private final static DataSourceFactoryImpl dataSourceFactory = new DataSourceFactoryImpl();

    public static void main(String[] args) throws Exception {
        System.out.println("Creating database...");
        CreateDb.run();
        System.out.println("Database create successfully.");
        Migrate.main(new String[]{"dev", "unit_test"});
    }

    private static void run() throws Exception {
        File file = new File("scripts/createdb.sql");
        String sql = FileUtils.readFileToString(file);
        Connection connection = DbUtil.createDataSourceWithoutDatabaseName(new DevEnvironment()).getConnection();
        MigrationUtil.executeSql(sql, connection);
        runSetupScripts();
    }

    private static void runSetupScripts() throws Exception {
        final File[] scripts = loadSetupScripts();
        for (File script : scripts) {
            System.out.println(script.getName());
            MigrationUtil.executeScript(script, dataSourceFactory.getDataSource(new DevEnvironment()).getConnection());
            MigrationUtil.executeScript(script, dataSourceFactory.getDataSource(new UnitTestEnvironment()).getConnection());
            MigrationUtil.executeScript(script, dataSourceFactory.getDataSource(new FunctionalTestEnvironment()).getConnection());
        }
    }

    private static File[] loadSetupScripts() {
        File dir = new File("scripts/setup");
        File[] filesArray = dir.listFiles();
        List<File> filesFromSetup = new ArrayList<>();
        if (filesArray != null) {
            filesFromSetup = Arrays.asList(filesArray);
        }
        final File[] totalArrayFiles = new File[filesFromSetup.size()];
        filesFromSetup.toArray(totalArrayFiles);
        return totalArrayFiles;
    }
}
