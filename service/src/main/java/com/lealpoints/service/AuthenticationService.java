package com.lealpoints.service;

import com.lealpoints.service.response.ServiceResult;

public interface AuthenticationService extends BaseService {
    public ServiceResult isValidApiKey(Integer userId, String apiKey);
}
