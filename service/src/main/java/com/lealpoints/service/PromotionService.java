package com.lealpoints.service;

import com.lealpoints.service.model.PromotionApplying;
import com.lealpoints.service.response.ServiceResult;

public interface PromotionService extends BaseService {

    ServiceResult<Long> applyPromotion(PromotionApplying promotionApplying);
}
