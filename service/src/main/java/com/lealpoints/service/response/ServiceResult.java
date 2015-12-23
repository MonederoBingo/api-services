package com.lealpoints.service.response;

public class ServiceResult<T> {

    private final boolean success;
    private final ServiceMessage message;
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

    public T getObject() {
        return object;
    }
}
