package com.lealpoints.migrations.util;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class MigrationUtil {
    public static void executeSql(String sql, Connection conn) {
        Statement st = null;
        try {
            st = conn.createStatement();
            st.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to execute SQL statement: " + sql);
        } finally {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
        }
    }

    public static void executeScript(File scriptFile, Connection connection) throws Exception {
        if (scriptFile.exists() && scriptFile.isFile()) {
            String sql;
            sql = FileUtils.readFileToString(scriptFile, "UTF-8");
            executeSql(sql, connection);
        } else {
            System.out.println("file doesn't exist: " + scriptFile.getName());
        }
    }
}
