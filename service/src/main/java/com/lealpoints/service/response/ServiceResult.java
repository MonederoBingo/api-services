package com.lealpoints.service.response;

public class ServiceResult<T> {

    private final boolean success;
    private final ServiceMessage message;
    private String extraInfo;
    private T object;

    public ServiceResult(boolean success, ServiceMessage message, T object) {
        this.success = success;
        this.message = message;
        this.object = object;
    }

    public ServiceResult(boolean success, ServiceMessage message) {
        this(success, message, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message.getMessage();
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

    public T getObject() {
        return object;
    }
}
