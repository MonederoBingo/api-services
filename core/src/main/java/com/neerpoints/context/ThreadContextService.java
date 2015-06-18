package com.neerpoints.context;

import com.neerpoints.db.QueryAgent;

public interface ThreadContextService {

    void initializeContext(boolean isProdEnvironment, String language);

    ThreadContext getThreadContext();

    QueryAgent getQueryAgent();

    void setThreadContextOnThread(ThreadContext threadContext);
}
