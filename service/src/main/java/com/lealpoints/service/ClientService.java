package com.lealpoints.service;

import com.lealpoints.model.CompanyClientMapping;
import com.lealpoints.service.model.ClientRegistration;
import com.lealpoints.service.response.ServiceResult;

import java.util.List;

public interface ClientService extends BaseService {

    ServiceResult<Long> register(ClientRegistration clientRegistration);

    ServiceResult<List<CompanyClientMapping>> getByCompanyId(long companyId);

    ServiceResult<CompanyClientMapping> getByCompanyIdPhone(long companyId, String phone);
}
