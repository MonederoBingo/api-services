package com.lealpoints.service;

import com.lealpoints.service.model.ClientRegistration;
import com.lealpoints.service.response.ServiceResult;

public interface ClientService extends BaseService {

    ServiceResult<Long> register(ClientRegistration clientRegistration);

    xyz.greatapp.libs.service.ServiceResult getByCompanyId(long companyId);

    xyz.greatapp.libs.service.ServiceResult getByCompanyIdPhone(long companyId, String phone);
}
