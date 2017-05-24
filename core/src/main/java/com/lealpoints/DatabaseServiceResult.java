package com.lealpoints;

public class DatabaseServiceResult<T> {
    private boolean success;
    private String message;
    private T object;
    private String extraInfo;

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
