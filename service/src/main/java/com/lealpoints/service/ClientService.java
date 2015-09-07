package com.lealpoints.service;

import java.util.List;
import com.lealpoints.model.CompanyClientMapping;
import com.lealpoints.service.model.ClientRegistration;
import com.lealpoints.service.model.ServiceResult;

public interface ClientService extends BaseService {

    public ServiceResult<Long> register(ClientRegistration clientRegistration);

    public ServiceResult<List<CompanyClientMapping>> getByCompanyId(long companyId);

    public ServiceResult<CompanyClientMapping> getByCompanyIdPhone(long companyId, String phone);
}
