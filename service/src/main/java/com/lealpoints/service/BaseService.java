package com.lealpoints.service;

import com.lealpoints.environments.Environment;
import com.lealpoints.util.Translations;

public interface BaseService {

    String getTranslation(Translations.Message message);

    boolean isProdEnvironment();

    Environment getEnvironment();
}
