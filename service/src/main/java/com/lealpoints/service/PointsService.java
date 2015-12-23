package com.lealpoints.service;

import com.lealpoints.service.model.PointsAwarding;
import com.lealpoints.service.response.ServiceResult;

public interface PointsService extends BaseService {
    ServiceResult<Float> awardPoints(PointsAwarding pointsAwarding);
}
