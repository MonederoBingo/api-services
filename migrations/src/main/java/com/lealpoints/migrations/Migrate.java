package com.lealpoints.migrations;

import javax.sql.DataSource;
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import com.lealpoints.db.DataSourceFactory;
import com.lealpoints.migrations.util.DBUtil;
import com.lealpoints.util.DateUtil;
import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Predicate;

public class Migrate {
    public static void main(String[] args) throws Exception {
        for (String arg : args) {
            switch (arg) {
                case "dev":
                    System.out.println("Running migrations for development...");
                    Migrate.run(DataSourceFactory.getDevDataSource());
                    break;
                case "unit_test":
                    System.out.println("Running migrations for unit test...");
                    Migrate.run(DataSourceFactory.getUnitTestDataSource());
                    break;
                case "functional_test":
                    System.out.println("Running migrations for functional test...");
                    Migrate.run(DataSourceFactory.getFunctionalTestDataSource());
                    break;
                case "prod":
                    System.out.println("Running migrations for production...");
                    Migrate.run(DataSourceFactory.getProdDataSource());
                    break;
                case "uat":
                    System.out.println("Running migrations for uat...");
                    Migrate.run(DataSourceFactory.getUATDataSource());
                    break;
            }
        }
        System.out.println("Process finished successfully.");
    }

    private static void run(DataSource dataSource) throws Exception {
        File[] files = loadMigrationScripts(dataSource);
        Connection connection = dataSource.getConnection();
        String lastFileExecuted = "";
        for (File file : files) {
            DBUtil.executeScript(file, connection);
            lastFileExecuted = file.getName();
        }
        if (!lastFileExecuted.equals("")) {
            final String lastMigrationString = lastFileExecuted.substring(0, lastFileExecuted.indexOf("_"));
            DBUtil.executeSql("UPDATE migration SET last_run_migration = " + lastMigrationString, connection);
        }
    }

    private static File[] loadMigrationScripts(DataSource dataSource) throws Exception {
        File dir = new File("scripts/migrations");
        File[] filesArray = dir.listFiles();
        List<File> filesFromMigration = new ArrayList<>();
        if (filesArray != null) {
            filesFromMigration = filterMigrationsByDate(Arrays.asList(filesArray), dataSource);
        }

        List<File> totalListFiles = new ArrayList<>();
        totalListFiles.addAll(filesFromMigration);

        final File[] totalArrayFiles = new File[totalListFiles.size()];
        totalListFiles.toArray(totalArrayFiles);
        return totalArrayFiles;
    }

    private static List<File> filterMigrationsByDate(List<File> filesFromSetup, DataSource dataSource) throws Exception {
        final Connection connection = dataSource.getConnection();
        Statement st = null;
        Collection<File> filteredFiles = filesFromSetup;
        try {
            String lastRunMigrationString = "19700101000000";
            st = connection.createStatement();
            final ResultSet resultSet = st.executeQuery("SELECT last_run_migration FROM migration;");
            if (resultSet.next()) {
                lastRunMigrationString = resultSet.getString("last_run_migration");
            }
            final Date lastRunMigrationDate = DateUtil.parseDate(lastRunMigrationString, "yyyyMMddHHmmss");
            filteredFiles = CollectionUtils.select(filesFromSetup, new Predicate<File>() {
                @Override
                public boolean evaluate(File file) {
                    final String fileName = file.getName();
                    if (fileName.charAt(14) == '_') {
                        String dateString = fileName.substring(0, fileName.indexOf("_"));
                        Date date = DateUtil.parseDate(dateString, "yyyyMMddHHmmss");
                        return date.after(lastRunMigrationDate);
                    } else {
                        throw new IllegalArgumentException("The file: " + fileName + " must contain an underscore '_' after the hash number.");
                    }
                }
            });
        } finally {
            if (st != null) {
                st.close();
            }
        }
        for (File filteredFile : filteredFiles) {
            System.out.println(filteredFile.getName());
        }
        return (List<File>) filteredFiles;
    }
}
