package com.neerpoints.repository;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.Statement;
import com.neerpoints.db.QueryAgent;
import com.neerpoints.db.UnitTestDatabaseManager;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;

/**
 * Defines basic behaviour for repository tests.
 */
public class BaseRepositoryTest {

    private QueryAgent _queryAgent;

    @Before
    public void setUpBase() throws Exception {
        _queryAgent = new QueryAgent(new UnitTestDatabaseManager());
        _queryAgent.beginTransaction();
    }

    @After
    public void tearDownBase() throws Exception {
        _queryAgent.rollbackTransaction();
    }

    protected void insertFixture(String fixturesFileName) throws Exception {
        final String fixturesDirectory = "../../neerpoints-services/database/scripts/fixtures/test/unit/";
        File file = new File(fixturesDirectory + fixturesFileName);
        executeFixtureFile(file);
    }

    protected QueryAgent getQueryAgent() {
        return _queryAgent;
    }

    private void executeFixtureFile(File scriptFile) throws Exception {
        if (scriptFile.exists() && scriptFile.isFile()) {
            String sql = FileUtils.readFileToString(scriptFile, "UTF-8");
            executeSql(sql, _queryAgent.getConnection());
        }
        else {
            throw new FileNotFoundException();
        }
    }

    private void executeSql(String sql, Connection conn) throws Exception {
        Statement st;
        st = conn.createStatement();
        st.execute(sql);
        st.close();
    }

    protected String encryptForSelect(String column, String wordToEncrypt) {
        return "crypt('" + wordToEncrypt + "', " + column + ")";
    }
}
