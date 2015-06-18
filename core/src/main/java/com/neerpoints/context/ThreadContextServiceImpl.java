package com.neerpoints.context;

import com.neerpoints.db.DatabaseManager;
import com.neerpoints.db.DevelopmentDatabaseManager;
import com.neerpoints.db.ProductionDatabaseManager;
import com.neerpoints.db.QueryAgent;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON, proxyMode = ScopedProxyMode.INTERFACES)
public class ThreadContextServiceImpl implements ThreadContextService {

    private static final ThreadLocal<ThreadContext> THREAD_CONTEXT = new ThreadLocal<>();

    @Override
    public void initializeContext(boolean isProdEnvironment, String language) {
        ThreadContext threadContext = new ThreadContext();
        DatabaseManager databaseManager;
        if (isProdEnvironment) {
            databaseManager = new ProductionDatabaseManager();
        } else {
            databaseManager = new DevelopmentDatabaseManager();
        }
        threadContext.setClientQueryAgent(new QueryAgent(databaseManager));
        threadContext.setLanguage(language);
        threadContext.setProdEnvironment(isProdEnvironment);
        setThreadContextOnThread(threadContext);
    }

    @Override
    public ThreadContext getThreadContext() {
        return THREAD_CONTEXT.get();
    }

    @Override
    public QueryAgent getQueryAgent() {
        return getThreadContext().getClientQueryAgent();
    }

    @Override
    public void setThreadContextOnThread(ThreadContext threadContext) {
        THREAD_CONTEXT.set(threadContext);
    }
}
