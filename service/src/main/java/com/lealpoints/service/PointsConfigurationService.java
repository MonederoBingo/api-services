package com.lealpoints.service;

import com.lealpoints.model.PointsConfiguration;
import com.lealpoints.service.response.ServiceResult;

public interface PointsConfigurationService {

    xyz.greatapp.libs.service.ServiceResult getByCompanyId(long companyId);

    ServiceResult<Boolean> update(PointsConfiguration pointsConfiguration);

    void registerPointsConfiguration(long companyId) throws Exception;
}
