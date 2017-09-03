package com.lealpoints.service;

import com.lealpoints.model.PromotionConfiguration;
import com.lealpoints.service.response.ServiceResult;

public interface PromotionConfigurationService extends BaseService {

    xyz.greatapp.libs.service.ServiceResult getByCompanyId(long companyId);

    ServiceResult<Long> insert(PromotionConfiguration promotionConfiguration);

    xyz.greatapp.libs.service.ServiceResult getByCompanyIdRequiredPoints(long companyId, final String phone);

    ServiceResult<Boolean> deletePromotionConfiguration(long promotionConfigurationId);
}
