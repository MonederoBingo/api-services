package com.lealpoints.service;

import com.lealpoints.service.model.CompanyLoginResult;
import com.lealpoints.service.model.CompanyUserLogin;
import com.lealpoints.service.model.CompanyUserPasswordChanging;
import com.lealpoints.service.model.CompanyUserRegistration;
import com.lealpoints.service.response.ServiceResult;

public interface CompanyUserService extends BaseService {

    ServiceResult<String> register(CompanyUserRegistration companyUserRegistration);

    ServiceResult<CompanyLoginResult> loginUser(CompanyUserLogin companyUserLogin);

    ServiceResult activateUser(String activationKey);

    ServiceResult sendActivationEmail(String email);

    ServiceResult sendTempPasswordEmail(String email);

    ServiceResult changePassword(CompanyUserPasswordChanging passwordChanging);
}
