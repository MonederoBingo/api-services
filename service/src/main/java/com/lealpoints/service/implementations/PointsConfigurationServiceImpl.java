package com.lealpoints.service.implementations;

import com.lealpoints.context.ThreadContextService;
import com.lealpoints.i18n.Message;
import com.lealpoints.model.PointsConfiguration;
import com.lealpoints.repository.PointsConfigurationRepository;
import com.lealpoints.service.PointsConfigurationService;
import com.lealpoints.service.model.ServiceResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PointsConfigurationServiceImpl extends BaseServiceImpl implements PointsConfigurationService {
    private static final Logger logger = LogManager.getLogger(PointsConfigurationServiceImpl.class.getName());
    private final PointsConfigurationRepository _pointsConfigurationRepository;

    @Autowired
    public PointsConfigurationServiceImpl(PointsConfigurationRepository pointsConfigurationRepository,
                                          ThreadContextService threadContextService) {
        super(threadContextService);
        _pointsConfigurationRepository = pointsConfigurationRepository;
    }

    public ServiceResult<PointsConfiguration> getByCompanyId(long companyId) {
        try {
            PointsConfiguration pointsConfiguration = _pointsConfigurationRepository.getByCompanyId(companyId);
            return new ServiceResult<>(true, "", pointsConfiguration);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return new ServiceResult<>(false, getTranslation(Message.COMMON_USER_ERROR), null);
        }
    }

    public ServiceResult<Boolean> update(PointsConfiguration pointsConfiguration) {
        try {
            int updatedRows = _pointsConfigurationRepository.update(pointsConfiguration);
            return new ServiceResult<>(true, getTranslation(Message.CONFIGURATION_UPDATED), updatedRows == 1);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return new ServiceResult<>(false, getTranslation(Message.COMMON_USER_ERROR), null);
        }
    }
}
