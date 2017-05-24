package com.lealpoints.service.response;

import com.lealpoints.i18n.Language;

public class ServiceResult<T> {
    private final boolean success;
    private final ServiceMessage serviceMessage;
    private final T object;
    private String extraInfo;

    public ServiceResult(boolean success, ServiceMessage serviceMessage, T object) {
        this(success, serviceMessage, object, "");
    }

    public ServiceResult(boolean success, ServiceMessage serviceMessage, T object, String extraInfo) {
        this.success = success;
        this.serviceMessage = serviceMessage;
        this.object = object;
        this.extraInfo = extraInfo;
    }

    public ServiceResult(boolean success, ServiceMessage serviceMessage) {
        this(success, serviceMessage, null, "");
    }


    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return serviceMessage.getMessage();
    }

    public ServiceMessage getServiceMessage()
    {
        return serviceMessage;
    }

    public String getTranslation(Language language) {
        return serviceMessage.getTranslation(language);
    }

    public T getObject() {
        return object;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }
}
