package com.lealpoints.service;

import com.lealpoints.service.model.ServiceResult;

public interface AuthenticationService extends BaseService {
    public ServiceResult isValidApiKey(Integer userId, String apiKey);
}
