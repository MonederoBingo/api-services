package com.lealpoints.service;

import com.lealpoints.service.model.PromotionApplying;
import com.lealpoints.service.model.ServiceResult;

public interface PromotionService extends BaseService {

    public ServiceResult<Long> applyPromotion(PromotionApplying promotionApplying);
}
