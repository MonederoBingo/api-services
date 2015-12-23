package com.lealpoints.service.response;

import com.lealpoints.i18n.Language;
import com.lealpoints.i18n.Message;

import java.util.HashMap;
import java.util.Map;

public class ServiceMessage {
    public final static ServiceMessage EMPTY = new ServiceMessage("");
    private final String message;
    private final Map<String, String> translations = new HashMap<>();

    public ServiceMessage(String message) {
        this.message = message;
    }

    public static ServiceMessage createServiceMessage(Message message, Language defaultLanguage, String... params) {
        ServiceMessage serviceMessage = new ServiceMessage(String.format(message.get(defaultLanguage), params));
        for (Language language : Language.values()) {
            serviceMessage.addTranslation(language, String.format(message.get(language), params));
        }
        return serviceMessage;
    }

    public void addTranslation(Language language, String message) {
        translations.put(language.getLangId(), message);
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return message;
    }
}
