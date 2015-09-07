package com.lealpoints.service;

import com.lealpoints.service.model.ClientLoginResult;
import com.lealpoints.service.model.ClientUserLogin;
import com.lealpoints.service.model.ClientUserRegistration;
import com.lealpoints.service.model.ServiceResult;

public interface ClientUserService extends BaseService {

    public ServiceResult<String> register(ClientUserRegistration clientUserRegistration);

    public ServiceResult<ClientLoginResult> login(ClientUserLogin clientUserLogin);

    public ServiceResult<Boolean> resendKey(String phone);
}
