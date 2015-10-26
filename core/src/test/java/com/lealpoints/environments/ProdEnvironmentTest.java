package com.lealpoints.environments;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ProdEnvironmentTest {

    @Test
    public void testDatabasePathAndCredentials() throws Exception {
        ProdEnvironment prodEnvironment = new ProdEnvironment();
        assertEquals("jdbc:postgresql://127.3.94.2:5432/services", prodEnvironment.getDatabasePath());
        assertEquals("org.postgresql.Driver", prodEnvironment.getDatabaseDriverClass());
        assertEquals("adminbpgiuam", prodEnvironment.getDatabaseUsername());
        assertEquals("VAmdITUgEGdg", prodEnvironment.getDatabasePassword());
    }
}