package com.neerpoints.service;

import com.neerpoints.context.Environment;
import com.neerpoints.context.ThreadContextService;
import com.neerpoints.util.Translations;

public class BaseService {

    private final Translations _translations;
    private final ThreadContextService _threadContextService;

    public BaseService(Translations translations, ThreadContextService threadContextService) {
        _translations = translations;
        _threadContextService = threadContextService;
    }

    String getTranslation(Translations.Message message) {
        return _translations.getMessage(message);
    }

    protected boolean isProdEnvironment() {
        return _threadContextService.getThreadContext().getEnvironment() == Environment.PROD;
    }

    protected Environment getEnvironment() {
        return _threadContextService.getThreadContext().getEnvironment();
    }
}
