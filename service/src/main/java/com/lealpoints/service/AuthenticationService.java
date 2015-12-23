package com.lealpoints.service;

import com.lealpoints.service.response.ServiceResult;

public interface AuthenticationService extends BaseService {
    ServiceResult isValidApiKey(String userId, String apiKey);
}
