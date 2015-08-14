package com.lealpoints.context;

import com.lealpoints.db.QueryAgent;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON, proxyMode = ScopedProxyMode.INTERFACES)
public class ThreadContextServiceImpl implements ThreadContextService {
    private static final ThreadLocal<ThreadContext> THREAD_CONTEXT = new ThreadLocal<>();

    @Override
    public void initializeContext(Environment environment, String language) {
        ThreadContext threadContext = new ThreadContext();
        threadContext.setClientQueryAgent(new QueryAgent(environment.getDatabaseManager()));
        threadContext.setLanguage(language);
        threadContext.setEnvironment(environment);
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
