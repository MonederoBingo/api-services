package com.lealpoints.repository;

import com.lealpoints.common.PropertyManager;
import com.lealpoints.db.datasources.DataSourceFactory;
import com.lealpoints.db.datasources.DataSourceFactoryImpl;
import com.lealpoints.db.queryagent.QueryAgent;
import com.lealpoints.db.queryagent.QueryAgentFactoryImpl;
import com.lealpoints.environments.EnvironmentFactory;
import com.lealpoints.environments.EnvironmentFactoryImpl;
import org.junit.After;
import org.junit.Before;
import java.sql.Connection;
import java.sql.Statement;

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

    protected QueryAgent getQueryAgent() {
        return _queryAgent;
    }

    protected void executeFixture(String script) throws Exception {
        if (!script.equals(null)) {
            String sql = script;
            executeSql(sql, _queryAgent.getConnection());
        } else {
            throw new NullPointerException();
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
