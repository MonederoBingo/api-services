package com.lealpoints.service.implementations;

import com.lealpoints.context.ThreadContext;
import com.lealpoints.context.ThreadContextService;
import com.lealpoints.db.queryagent.QueryAgent;
import com.lealpoints.environments.Environment;
import com.lealpoints.environments.ProdEnvironment;
import com.lealpoints.environments.UATEnvironment;
import com.lealpoints.service.BaseService;
import com.lealpoints.util.Translations;

public class BaseServiceImpl implements BaseService {

    private final Translations _translations;
    private final ThreadContextService _threadContextService;

    public BaseServiceImpl(Translations translations, ThreadContextService threadContextService) {
        _translations = translations;
        _threadContextService = threadContextService;
    }

    public String getTranslation(Translations.Message message) {
        return _translations.getMessage(message);
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
