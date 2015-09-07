package com.lealpoints.service;

import com.lealpoints.service.model.CompanyLoginResult;
import com.lealpoints.service.model.CompanyUserLogin;
import com.lealpoints.service.model.CompanyUserPasswordChanging;
import com.lealpoints.service.model.ServiceResult;

public interface CompanyUserService extends BaseService {

    public ServiceResult<CompanyLoginResult> loginUser(CompanyUserLogin companyUserLogin);

    public ServiceResult activateUser(String activationKey);

    public ServiceResult sendActivationEmail(String email);

    public ServiceResult sendTempPasswordEmail(String email);

    public ServiceResult changePassword(CompanyUserPasswordChanging passwordChanging);
}
