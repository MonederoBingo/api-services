package com.lealpoints.service.implementations;

import com.lealpoints.i18n.Language;
import com.lealpoints.i18n.Message;
import com.lealpoints.service.BaseService;
import com.lealpoints.service.response.ServiceMessage;
import xyz.greatapp.libs.service.Environment;
import xyz.greatapp.libs.service.context.ThreadContext;
import xyz.greatapp.libs.service.context.ThreadContextService;

public class BaseServiceImpl implements BaseService {

    private final ThreadContextService _threadContextService;

    public BaseServiceImpl(ThreadContextService threadContextService) {
        _threadContextService = threadContextService;
    }

    public ServiceMessage getServiceMessage(Message message, String... params) {
        return ServiceMessage.createServiceMessage(message, Language.ENGLISH, params);
    }

    public boolean isProdEnvironment() {
        return _threadContextService.getThreadContext().getEnvironment() == xyz.greatapp.libs.service.Environment.PROD;
    }

    public boolean isUATEnvironment() {
        return _threadContextService.getThreadContext().getEnvironment() == Environment.UAT;
    }

    public boolean isDevEnvironment() {
        return _threadContextService.getThreadContext().getEnvironment() == Environment.DEV;
    }

    public boolean isFunctionalTestEnvironment() {
        return _threadContextService.getThreadContext().getEnvironment() == Environment.AUTOMATION_TEST;
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
}
