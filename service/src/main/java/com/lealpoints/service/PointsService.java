package com.lealpoints.service;

import com.lealpoints.service.model.PointsAwarding;
import com.lealpoints.service.model.ServiceResult;

public interface PointsService extends BaseService {
    public ServiceResult<Float> awardPoints(PointsAwarding pointsAwarding);
}
