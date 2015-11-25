package com.lealpoints.service;

import com.lealpoints.environments.Environment;
import com.lealpoints.i18n.Message;

public interface BaseService {

    String getTranslation(Message message);

    boolean isProdEnvironment();

    Environment getEnvironment();
}
