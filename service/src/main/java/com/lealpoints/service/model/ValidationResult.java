package com.lealpoints.service.model;

public class ValidationResult {

    private final boolean _isValid;
    private final String message;

    public ValidationResult(boolean _isValid, String message) {
        this._isValid = _isValid;
        this.message = message;
    }

    public ValidationResult(boolean _isValid) {
        this(_isValid, "");
    }

    public boolean isValid() {
        return _isValid;
    }

    public boolean isInvalid() {
        return !_isValid;
    }

    public String getMessage() {
        return message;
    }
}
