package com.lealpoints.repository;

import com.lealpoints.context.ThreadContextService;
import com.lealpoints.db.queryagent.QueryAgent;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseRepository {

    @Autowired
    private ThreadContextService _threadContextService;


    protected QueryAgent getQueryAgent() throws Exception {
        return _threadContextService.getQueryAgent();
    }

}
