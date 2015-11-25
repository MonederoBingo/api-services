package com.lealpoints.service.implementations;

import com.lealpoints.context.ThreadContext;
import com.lealpoints.context.ThreadContextService;
import com.lealpoints.db.queryagent.QueryAgent;
import com.lealpoints.environments.Environment;
import com.lealpoints.environments.ProdEnvironment;
import com.lealpoints.environments.UATEnvironment;
import com.lealpoints.i18n.Message;
import com.lealpoints.service.BaseService;

public class BaseServiceImpl implements BaseService {

    private final ThreadContextService _threadContextService;

    public BaseServiceImpl(ThreadContextService threadContextService) {
        _threadContextService = threadContextService;
    }

    public String getTranslation(Message message) {
        return message.get(getThreadContext().getLanguage());
    }

    public boolean isProdEnvironment() {
        return _threadContextService.getThreadContext().getEnvironment() instanceof ProdEnvironment;
    }

    public boolean isUATEnvironment() {
        return _threadContextService.getThreadContext().getEnvironment() instanceof UATEnvironment;
    }

    public Environment getEnvironment() {
        return _threadContextService.getThreadContext().getEnvironment();
    }

    public ThreadContextService getThreadContextService() {
        return _threadContextService;
    }

    public ThreadContext getThreadContext() {
        return _threadContextService.getThreadContext();
    }

    public QueryAgent getQueryAgent() {
        return _threadContextService.getQueryAgent();
    }
}
