package com.lealpoints.service;

import com.lealpoints.context.ThreadContextService;
import com.lealpoints.db.QueryAgent;
import com.lealpoints.model.Client;
import com.lealpoints.model.CompanyClientMapping;
import com.lealpoints.model.Points;
import com.lealpoints.model.PointsConfiguration;
import com.lealpoints.repository.ClientRepository;
import com.lealpoints.repository.CompanyClientMappingRepository;
import com.lealpoints.repository.PointsConfigurationRepository;
import com.lealpoints.repository.PointsRepository;
import com.lealpoints.service.base.BaseService;
import com.lealpoints.service.model.PointsAwarding;
import com.lealpoints.service.model.ServiceResult;
import com.lealpoints.service.model.ValidationResult;
import com.lealpoints.service.validation.PhoneValidatorService;
import com.lealpoints.util.DateUtil;
import com.lealpoints.util.Translations;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PointsService extends BaseService {
    private static final Logger logger = LogManager.getLogger(PointsService.class.getName());
    private final PointsRepository _pointsRepository;
    private final PointsConfigurationRepository _pointsConfigurationRepository;
    private final ClientRepository _clientRepository;
    private final CompanyClientMappingRepository _companyClientMappingRepository;
    private final ThreadContextService _threadContextService;
    private final PhoneValidatorService _phoneValidatorService;

    @Autowired
    public PointsService(PointsRepository pointsRepository, PointsConfigurationRepository pointsConfigurationRepository,
        ClientRepository clientRepository, CompanyClientMappingRepository companyClientMappingRepository, ThreadContextService threadContextService,
        Translations translations, PhoneValidatorService phoneValidatorService) {
        super(translations, threadContextService);
        _pointsRepository = pointsRepository;
        _pointsConfigurationRepository = pointsConfigurationRepository;
        _clientRepository = clientRepository;
        _companyClientMappingRepository = companyClientMappingRepository;
        _threadContextService = threadContextService;
        _phoneValidatorService = phoneValidatorService;
    }

    public ServiceResult<Float> awardPoints(PointsAwarding pointsAwarding) {
        try {
            ValidationResult validationResult = validateRegistration(pointsAwarding);
            if (validationResult.isValid()) {
                final QueryAgent queryAgent = _threadContextService.getQueryAgent();
                queryAgent.beginTransaction();
                float earnedPoints = awardPointsAndUpdateClientStatus(pointsAwarding);
                queryAgent.commitTransaction();
                if (earnedPoints > 0) {
                    return new ServiceResult<>(true, getTranslation(Translations.Message.POINTS_AWARDED) + ": " + earnedPoints, earnedPoints);
                } else {
                    return new ServiceResult<>(true, getTranslation(Translations.Message.THE_CLIENT_DID_NOT_GET_POINTS), earnedPoints);
                }
            } else {
                return new ServiceResult<>(false, validationResult.getMessage());
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return new ServiceResult<>(false, getTranslation(Translations.Message.COMMON_USER_ERROR), null);
        }
    }

    private float awardPointsAndUpdateClientStatus(PointsAwarding pointsAwarding) throws Exception {
        //Inserting client if it doesn't exist
        PointsConfiguration pointsConfiguration = _pointsConfigurationRepository.getByCompanyId(pointsAwarding.getCompanyId());
        if (pointsConfiguration == null) {
            throw new IllegalArgumentException("Points configuration doesn't exist");
        }
            Client client = _clientRepository.insertIfDoesNotExist(pointsAwarding.getPhone(), true);
            //Inserting company client mapping if it doesn't exist
            _companyClientMappingRepository.insertIfDoesNotExist(pointsAwarding.getCompanyId(), client.getClientId());

            Points points = new Points();
            points.setCompanyId(pointsAwarding.getCompanyId());
            points.setClientId(client.getClientId());
            points.setSaleKey(pointsAwarding.getSaleKey());
            points.setRequiredAmount(pointsConfiguration.getRequiredAmount());
            points.setPointsToEarn(pointsConfiguration.getPointsToEarn());
            points.setSaleAmount(pointsAwarding.getSaleAmount());
            points.setEarnedPoints(calculateEarnedPoints(points, pointsAwarding.getSaleAmount(), pointsConfiguration.getRequiredAmount()));
            points.setDate(DateUtil.dateNow());

            //Inserting points for this client
            _pointsRepository.insert(points);

            // Updating points in company client mapping table
            CompanyClientMapping companyClientMapping =
                _companyClientMappingRepository.getByCompanyIdClientId(pointsAwarding.getCompanyId(), client.getClientId());
            if (companyClientMapping == null) {
                throw new IllegalArgumentException("CompanyClientMapping doesn't exist.");
            }
            companyClientMapping.setPoints(companyClientMapping.getPoints() + points.getEarnedPoints());
            _companyClientMappingRepository.updatePoints(companyClientMapping);
            return points.getEarnedPoints();
    }

    private float calculateEarnedPoints(Points points, float saleAmount, float requiredAmount) {
        if (saleAmount >= requiredAmount) {
            return (int) (points.getSaleAmount() / points.getRequiredAmount() * points.getPointsToEarn());
        } else {
            return 0;
        }
    }

    private ValidationResult validateRegistration(PointsAwarding pointsAwarding) throws Exception {
        final ValidationResult phoneValidation = _phoneValidatorService.validate(pointsAwarding.getPhone());
        if (phoneValidation.isInvalid()) {
            return phoneValidation;
        }
        Points points = _pointsRepository.getByCompanyIdSaleKey(pointsAwarding.getCompanyId(), pointsAwarding.getSaleKey());
        if (points != null) {
            return new ValidationResult(false, getTranslation(Translations.Message.SALE_KEY_ALREADY_EXISTS));
        }
        return new ValidationResult(true, "");
    }
}
