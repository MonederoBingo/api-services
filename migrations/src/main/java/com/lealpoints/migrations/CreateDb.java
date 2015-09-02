package com.lealpoints.migrations;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.lealpoints.migrations.db.DevelopmentDatabaseManager;
import com.lealpoints.migrations.db.FunctionalTestDatabaseManager;
import com.lealpoints.migrations.db.UnitTestDatabaseManager;
import com.lealpoints.migrations.util.DBUtil;
import org.apache.commons.io.FileUtils;

public class CreateDb {

    private DevelopmentDatabaseManager _developmentDatabaseManager = new DevelopmentDatabaseManager();

    public static void main(String[] args) throws Exception {
        System.out.println("Creating database...");
        CreateDb createDb = new CreateDb();
        createDb.run();
        System.out.println("Database create successfully.");
        Migrate.main(null);
    }

    private void run() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        final URL resource = classLoader.getResource("scripts/createdb.sql");
        if (resource != null) {
            String sql = FileUtils.readFileToString(new File(resource.getFile()));
            Connection connection = _developmentDatabaseManager.getConnection(false);
            DBUtil.executeSql(sql, connection);
            runSetupScripts();
        }
    }

    private void runSetupScripts() throws Exception {
        final File[] scripts = loadSetupScripts();
        for (File script : scripts) {
            System.out.println(script.getName());
            DBUtil.executeScript(script, new DevelopmentDatabaseManager().getConnection());
            DBUtil.executeScript(script, new UnitTestDatabaseManager().getConnection());
            DBUtil.executeScript(script, new FunctionalTestDatabaseManager().getConnection());
        }
    }

    private File[] loadSetupScripts() {
        ClassLoader classLoader = getClass().getClassLoader();
        final URL resource = classLoader.getResource("scripts/setup/");
        if (resource == null) {
            return new File[0];
        }
        File dir = new File(resource.getFile());
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
