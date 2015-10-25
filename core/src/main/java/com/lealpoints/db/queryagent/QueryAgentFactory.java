package com.lealpoints.db.queryagent;

import com.lealpoints.environments.Environment;

public interface QueryAgentFactory {
    QueryAgent getQueryAgent(Environment environment);
}
