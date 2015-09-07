package com.lealpoints.service.implementations;

import com.lealpoints.context.Environment;
import com.lealpoints.context.ThreadContextService;
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
        return _threadContextService.getThreadContext().getEnvironment() == Environment.PROD;
    }

    public Environment getEnvironment() {
        return _threadContextService.getThreadContext().getEnvironment();
    }
}
