package com.lealpoints.service.implementations;

import com.lealpoints.context.ThreadContext;
import com.lealpoints.context.ThreadContextService;
import com.lealpoints.db.queryagent.QueryAgent;
import com.lealpoints.environments.Environment;
import com.lealpoints.environments.ProdEnvironment;
import com.lealpoints.environments.UATEnvironment;
import com.lealpoints.i18n.Language;
import com.lealpoints.i18n.Message;
import com.lealpoints.service.BaseService;
import com.lealpoints.service.response.ServiceMessage;

public class BaseServiceImpl implements BaseService {

    private final ThreadContextService _threadContextService;

    public BaseServiceImpl(ThreadContextService threadContextService) {
        _threadContextService = threadContextService;
    }

    public ServiceMessage getServiceMessage(Message message, String... params) {
        ServiceMessage serviceMessage = new ServiceMessage(String.format(message.get(getThreadContext().getLanguage()), params));
        for (Language language : Language.values()) {
            serviceMessage.addTranslation(language, String.format(message.get(language), params));
        }
        return serviceMessage;
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
