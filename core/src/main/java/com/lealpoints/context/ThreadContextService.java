package com.lealpoints.context;

import com.lealpoints.db.queryagent.QueryAgent;
import com.lealpoints.environments.Environment;

public interface ThreadContextService {

    void initializeContext(Environment env, String language);

    ThreadContext getThreadContext();

    Environment getEnvironment();

    QueryAgent getQueryAgent();

    void setThreadContextOnThread(ThreadContext threadContext);
}
