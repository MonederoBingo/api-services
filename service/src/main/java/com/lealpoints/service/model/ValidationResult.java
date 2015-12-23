package com.lealpoints.service.model;

import com.lealpoints.service.response.ServiceMessage;

public class ValidationResult {

    private final boolean _isValid;
    private final ServiceMessage _serviceMessage;

    public ValidationResult(boolean isValid, ServiceMessage message) {
        _isValid = isValid;
        _serviceMessage = message;
    }

    public ValidationResult(boolean isValid) {
        this(isValid, ServiceMessage.EMPTY);
    }

    public boolean isValid() {
        return _isValid;
    }

    public boolean isInvalid() {
        return !_isValid;
    }

    public ServiceMessage getServiceMessage() {
        return _serviceMessage;
    }
}
