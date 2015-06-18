package com.neerpoints.service.model;

public class ServiceResult<T> {

    private final boolean success;
    private final String message;
    private T object;

    public ServiceResult(boolean success, String message, T object) {
        this.success = success;
        this.message = message;
        this.object = object;
    }

    public ServiceResult(boolean success, String message) {
        this(success, message, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public T getObject() {
        return object;
    }
}
