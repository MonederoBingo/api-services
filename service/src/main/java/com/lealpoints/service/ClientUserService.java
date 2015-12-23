package com.lealpoints.service;

import com.lealpoints.service.model.ClientLoginResult;
import com.lealpoints.service.model.ClientUserLogin;
import com.lealpoints.service.model.ClientUserRegistration;
import com.lealpoints.service.response.ServiceResult;

public interface ClientUserService extends BaseService {

    ServiceResult<String> register(ClientUserRegistration clientUserRegistration);

    ServiceResult<ClientLoginResult> login(ClientUserLogin clientUserLogin);

    ServiceResult<Boolean> resendKey(String phone);
}
