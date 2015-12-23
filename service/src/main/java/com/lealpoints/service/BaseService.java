package com.lealpoints.service;

import com.lealpoints.environments.Environment;
import com.lealpoints.i18n.Message;
import com.lealpoints.service.response.ServiceMessage;

public interface BaseService {

    ServiceMessage getServiceMessage(Message message, String... params);

    boolean isProdEnvironment();

    Environment getEnvironment();
}
