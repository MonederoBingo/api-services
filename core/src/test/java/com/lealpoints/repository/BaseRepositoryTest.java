package com.lealpoints.repository;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.Statement;
import com.lealpoints.common.PropertyManager;
import com.lealpoints.db.datasources.DataSourceFactory;
import com.lealpoints.db.datasources.DataSourceFactoryImpl;
import com.lealpoints.db.queryagent.QueryAgent;
import com.lealpoints.db.queryagent.QueryAgentFactoryImpl;
import com.lealpoints.environments.EnvironmentFactory;
import com.lealpoints.environments.EnvironmentFactoryImpl;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;

public class BaseRepositoryTest {

    private static QueryAgent _queryAgent;

    static {
        PropertyManager.reloadConfiguration();
        loadQueryAgent();
    }

    private static void loadQueryAgent() {
        DataSourceFactory dataSourceFactory = new DataSourceFactoryImpl();
        EnvironmentFactory environmentFactory = new EnvironmentFactoryImpl();
        _queryAgent = new QueryAgentFactoryImpl(dataSourceFactory).getQueryAgent(environmentFactory.getUnitTestEnvironment());
    }

    @Before
    public void setUpBase() throws Exception {
        _queryAgent.beginTransaction();
    }

    @After
    public void tearDownBase() throws Exception {
        _queryAgent.rollbackTransaction();
    }

    protected void insertFixture(String fixturesFileName) throws Exception {
       final String fixturesDirectory = PropertyManager.getProperty("unit_test.fixture_dir");
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
        } else {
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
