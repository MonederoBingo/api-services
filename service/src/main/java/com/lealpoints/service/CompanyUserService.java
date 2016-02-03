package com.lealpoints.service;

import com.lealpoints.model.CompanyUser;
import com.lealpoints.service.model.CompanyLoginResult;
import com.lealpoints.service.model.CompanyUserLogin;
import com.lealpoints.service.model.CompanyUserPasswordChanging;
import com.lealpoints.service.model.CompanyUserRegistration;
import com.lealpoints.service.response.ServiceResult;

import java.util.List;

public interface CompanyUserService extends BaseService {

    ServiceResult<String> register(CompanyUserRegistration companyUserRegistration);

    ServiceResult<List<CompanyUser>> getByCompanyId(long companyId);

    ServiceResult<CompanyLoginResult> loginUser(CompanyUserLogin companyUserLogin);

    ServiceResult activateUser(String activationKey);

    ServiceResult sendActivationEmail(String email);

    ServiceResult sendTempPasswordEmail(String email);

    ServiceResult changePassword(CompanyUserPasswordChanging passwordChanging);
}
