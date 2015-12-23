package com.lealpoints.service.response;

import com.lealpoints.i18n.Language;

import java.util.HashMap;
import java.util.Map;

public class ServiceMessage {
    public final static ServiceMessage EMPTY = new ServiceMessage("");
    private final String message;
    private final Map<Language, String> translations = new HashMap<>();

    public ServiceMessage(String message) {
        this.message = message;
    }

    public void addTranslation(Language language, String message) {
        translations.put(language, message);
    }

    public Map<Language, String> getTranslations() {
        return translations;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return message;
    }
}
