package com.lealpoints.service;

import com.lealpoints.service.model.ValidationResult;

public interface PhoneValidatorService extends BaseService {

    public ValidationResult validate(String phone);
}
