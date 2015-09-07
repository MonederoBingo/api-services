package com.lealpoints.service;

import java.util.List;
import com.lealpoints.model.PromotionConfiguration;
import com.lealpoints.service.model.ServiceResult;

public interface PromotionConfigurationService extends BaseService {

    public ServiceResult<List<PromotionConfiguration>> getByCompanyId(long companyId);

    public ServiceResult<Long> insert(PromotionConfiguration promotionConfiguration);

    public ServiceResult<List<PromotionConfiguration>> getByCompanyIdRequiredPoints(long companyId, final String phone);

    public ServiceResult<Boolean> deletePromotionConfiguration(long promotionConfigurationId);
}
