package com.lealpoints.service;

import com.lealpoints.model.PointsConfiguration;
import com.lealpoints.service.model.ServiceResult;

public interface PointsConfigurationService extends BaseService {

    public ServiceResult<PointsConfiguration> getByCompanyId(long companyId);

    public ServiceResult<Boolean> update(PointsConfiguration pointsConfiguration);
}
