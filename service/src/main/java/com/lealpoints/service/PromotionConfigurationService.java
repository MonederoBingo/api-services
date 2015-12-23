package com.lealpoints.service;

import com.lealpoints.model.PromotionConfiguration;
import com.lealpoints.service.response.ServiceResult;

import java.util.List;

public interface PromotionConfigurationService extends BaseService {

    ServiceResult<List<PromotionConfiguration>> getByCompanyId(long companyId);

    ServiceResult<Long> insert(PromotionConfiguration promotionConfiguration);

    ServiceResult<List<PromotionConfiguration>> getByCompanyIdRequiredPoints(long companyId, final String phone);

    ServiceResult<Boolean> deletePromotionConfiguration(long promotionConfigurationId);
}
