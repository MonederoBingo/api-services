package com.neerpoints.migrations.util;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import com.neerpoints.migrations.db.DatabaseManager;
import com.neerpoints.migrations.db.ProductionDatabaseManager;
import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Predicate;

public class MigrateProduction {
    public static void main(String[] args) throws Exception {
        System.out.println("Running migrations for production...");
        new MigrateProduction().run(new ProductionDatabaseManager());
        System.out.println("Process finished successfully.");
    }

    private void run(DatabaseManager abstractDatabaseManager) throws Exception {
        File[] files = loadMigrationScripts(abstractDatabaseManager);
        Connection connection = abstractDatabaseManager.getConnection();
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

    private File[] loadMigrationScripts(DatabaseManager databaseManager) throws Exception {
        File dir = new File("./database/scripts/migration");
        File[] filesArray = dir.listFiles();
        List<File> filesFromMigration = new ArrayList<>();
        if (filesArray != null) {
            filesFromMigration = filterMigrationsByDate(Arrays.asList(filesArray), databaseManager);
        }

        List<File> totalListFiles = new ArrayList<>();
        totalListFiles.addAll(filesFromMigration);

        final File[] totalArrayFiles = new File[totalListFiles.size()];
        totalListFiles.toArray(totalArrayFiles);
        return totalArrayFiles;
    }

    private List<File> filterMigrationsByDate(List<File> filesFromSetup, DatabaseManager abstractDatabaseManager) throws Exception {
        final Connection connection = abstractDatabaseManager.getConnection();
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
                    String dateString = fileName.substring(0, fileName.indexOf("_"));
                    Date date = DateUtil.parseDate(dateString, "yyyyMMddHHmmss");
                    return date.after(lastRunMigrationDate);
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
