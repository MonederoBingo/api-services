package com.lealpoints.i18n;

import java.util.HashMap;
import java.util.Map;

public enum Language {
    ENGLISH("en", "US"),
    SPANISH("es", "MX");

    private static Map<String, Language> languageIds = new HashMap<>();

    static {
        for (Language language : values()) {
            languageIds.put(language.getLangId(), language);
        }
    }

    private final String langId;
    private final String countryId;

    Language(String langId, String countryId) {
        this.langId = langId;
        this.countryId = countryId;
    }

    public static Language getByLangId(String langId) {
        return languageIds.get(langId);
    }

    public String getLangId() {
        return langId;
    }

    public String getCountryId() {
        return countryId;
    }
}
