package com.lealpoints.service.implementations;

import com.lealpoints.context.ThreadContextService;
import com.lealpoints.db.queryagent.QueryAgent;
import com.lealpoints.i18n.Message;
import com.lealpoints.model.CompanyClientMapping;
import com.lealpoints.model.Points;
import com.lealpoints.model.PointsConfiguration;
import com.lealpoints.repository.ClientRepository;
import com.lealpoints.repository.CompanyClientMappingRepository;
import com.lealpoints.repository.PointsRepository;
import com.lealpoints.service.PointsService;
import com.lealpoints.service.model.PointsAwarding;
import com.lealpoints.service.model.ValidationResult;
import com.lealpoints.service.response.ServiceMessage;
import com.lealpoints.service.response.ServiceResult;
import com.lealpoints.util.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PointsServiceImpl extends BaseServiceImpl implements PointsService {
    private static final Logger logger = LogManager.getLogger(PointsServiceImpl.class.getName());
    private final PointsRepository _pointsRepository;
    private final ClientRepository _clientRepository;
    private final CompanyClientMappingRepository _companyClientMappingRepository;
    private final PhoneValidatorServiceImpl _phoneValidatorService;
    private final PointsConfigurationServiceImpl pointsConfigurationService;

    @Autowired
    public PointsServiceImpl(PointsRepository pointsRepository,
                             ClientRepository clientRepository, CompanyClientMappingRepository companyClientMappingRepository, ThreadContextService threadContextService,
                             PhoneValidatorServiceImpl phoneValidatorService, PointsConfigurationServiceImpl pointsConfigurationService) {
        super(threadContextService);
        _pointsRepository = pointsRepository;
        _clientRepository = clientRepository;
        _companyClientMappingRepository = companyClientMappingRepository;
        _phoneValidatorService = phoneValidatorService;
        this.pointsConfigurationService = pointsConfigurationService;
    }

    public ServiceResult<Float> awardPoints(PointsAwarding pointsAwarding) {
        try {
            ValidationResult validationResult = validateRegistration(pointsAwarding);
            if (validationResult.isValid()) {
                final QueryAgent queryAgent = getQueryAgent();
                queryAgent.beginTransaction();
                float earnedPoints = awardPointsAndUpdateClientStatus(pointsAwarding);
                queryAgent.commitTransaction();
                if (earnedPoints > 0) {
                    return new ServiceResult<>(true, getServiceMessage(Message.POINTS_AWARDED, "" + earnedPoints), earnedPoints);
                } else {
                    return new ServiceResult<>(true, getServiceMessage(Message.THE_CLIENT_DID_NOT_GET_POINTS), earnedPoints);
                }
            } else {
                return new ServiceResult<>(false, validationResult.getServiceMessage());
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return new ServiceResult<>(false, getServiceMessage(Message.COMMON_USER_ERROR), null);
        }
    }

    private float awardPointsAndUpdateClientStatus(PointsAwarding pointsAwarding) throws Exception {
        //Inserting client if it doesn't exist
        PointsConfiguration pointsConfiguration = pointsConfigurationService.getByCompanyId(pointsAwarding.getCompanyId()).getObject();
        if (pointsConfiguration == null) {
            throw new IllegalArgumentException("Points configuration doesn't exist");
        }
        xyz.greatapp.libs.service.ServiceResult client = _clientRepository.insertIfDoesNotExist(pointsAwarding.getPhoneNumber(), true);
        //Inserting company client mapping if it doesn't exist
        long clientId = new JSONObject(client.getObject()).getLong("client_id");
        _companyClientMappingRepository.insertIfDoesNotExist(pointsAwarding.getCompanyId(), clientId);

        Points points = new Points();
        points.setCompanyId(pointsAwarding.getCompanyId());
        points.setClientId(clientId);
        points.setSaleKey(pointsAwarding.getSaleKey());
        points.setRequiredAmount(pointsConfiguration.getRequiredAmount());
        points.setPointsToEarn(pointsConfiguration.getPointsToEarn());
        points.setSaleAmount(pointsAwarding.getSaleAmount());
        points.setEarnedPoints(calculateEarnedPoints(points, pointsAwarding.getSaleAmount(), pointsConfiguration.getRequiredAmount()));
        points.setDate(DateUtil.dateNow());

        //Inserting points for this client
        _pointsRepository.insert(points);

        // Updating points in company client mapping table
        xyz.greatapp.libs.service.ServiceResult serviceResult =
                _companyClientMappingRepository.getByCompanyIdClientId(pointsAwarding.getCompanyId(), clientId);
        if ("{}".equals(serviceResult.getObject())) {
            throw new IllegalArgumentException("CompanyClientMapping doesn't exist.");
        }
        CompanyClientMapping companyClientMapping = CompanyClientMapping.fromJSONObject(new JSONObject(serviceResult.getObject()));
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
        final ValidationResult phoneValidation = _phoneValidatorService.validate(pointsAwarding.getPhoneNumber());
        if (phoneValidation.isInvalid()) {
            return phoneValidation;
        }
        if (StringUtils.isEmpty(pointsAwarding.getSaleKey())) {
            return new ValidationResult(false, getServiceMessage(Message.EMPTY_SALE_KEY));
        }
        Points points = _pointsRepository.getByCompanyIdSaleKey(pointsAwarding.getCompanyId(), pointsAwarding.getSaleKey());
        if (points != null) {
            return new ValidationResult(false, getServiceMessage(Message.SALE_KEY_ALREADY_EXISTS));
        }
        return new ValidationResult(true, ServiceMessage.EMPTY);
    }
}
