package com.lealpoints.service;

import com.lealpoints.model.PointsConfiguration;
import com.lealpoints.service.response.ServiceResult;

public interface PointsConfigurationService {

    ServiceResult<PointsConfiguration> getByCompanyId(long companyId);

    ServiceResult<Boolean> update(PointsConfiguration pointsConfiguration);
}
