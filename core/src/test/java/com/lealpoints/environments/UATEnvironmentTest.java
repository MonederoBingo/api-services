package com.lealpoints.environments;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UATEnvironmentTest {
    @Test
    public void testDatabasePathAndCredentials() throws Exception {
        UATEnvironment prodEnuatEnvironmentironment = new UATEnvironment();
        assertEquals("jdbc:postgresql://127.3.94.2:5432/uat", prodEnuatEnvironmentironment.getDatabasePath());
        assertEquals("org.postgresql.Driver", prodEnuatEnvironmentironment.getDatabaseDriverClass());
        assertEquals("adminbpgiuam", prodEnuatEnvironmentironment.getDatabaseUsername());
        assertEquals("VAmdITUgEGdg", prodEnuatEnvironmentironment.getDatabasePassword());
    }
}