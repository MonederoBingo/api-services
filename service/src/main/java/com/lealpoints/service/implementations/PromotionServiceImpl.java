package com.lealpoints.service.implementations;

import xyz.greatapp.libs.service.context.ThreadContextService;
import com.lealpoints.i18n.Message;
import com.lealpoints.model.CompanyClientMapping;
import com.lealpoints.model.Promotion;
import com.lealpoints.model.PromotionConfiguration;
import com.lealpoints.repository.ClientRepository;
import com.lealpoints.repository.CompanyClientMappingRepository;
import com.lealpoints.repository.PromotionConfigurationRepository;
import com.lealpoints.repository.PromotionRepository;
import com.lealpoints.service.PromotionService;
import com.lealpoints.service.model.PromotionApplying;
import com.lealpoints.service.response.ServiceResult;
import com.lealpoints.util.DateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PromotionServiceImpl extends BaseServiceImpl implements PromotionService {
    private static final Logger logger = LogManager.getLogger(PromotionServiceImpl.class.getName());
    private final PromotionRepository _promotionRepository;
    private final PromotionConfigurationRepository _promotionConfigurationRepository;
    private final CompanyClientMappingRepository _companyClientMappingRepository;
    private final ClientRepository _clientRepository;

    @Autowired
    public PromotionServiceImpl(PromotionRepository promotionRepository, PromotionConfigurationRepository promotionConfigurationRepository,
                                CompanyClientMappingRepository companyClientMappingRepository, ClientRepository clientRepository, ThreadContextService threadContextService) {
        super(threadContextService);
        _promotionRepository = promotionRepository;
        _promotionConfigurationRepository = promotionConfigurationRepository;
        _companyClientMappingRepository = companyClientMappingRepository;
        _clientRepository = clientRepository;
    }

    public ServiceResult<Long> applyPromotion(PromotionApplying promotionApplying) {
        try {
            final xyz.greatapp.libs.service.ServiceResult client = _clientRepository.getByPhone(promotionApplying.getPhoneNumber());
            if (client.getObject().equals("{}")) {
                return new ServiceResult<>(false, getServiceMessage(Message.PHONE_NUMBER_DOES_NOT_EXIST));
            }

            xyz.greatapp.libs.service.ServiceResult serviceResult = _companyClientMappingRepository.getByCompanyIdClientId(
                    promotionApplying.getCompanyId(), new JSONObject(client.getObject()).getLong("client_id"));
            xyz.greatapp.libs.service.ServiceResult promotionConfiguration = _promotionConfigurationRepository.getById(
                    promotionApplying.getPromotionConfigurationId());

            if (new JSONObject(serviceResult.getObject()).getDouble("points") <
                    new JSONObject(promotionConfiguration.getObject()).getDouble("required_points")) {
                return new ServiceResult<>(false, getServiceMessage(Message.CLIENT_DOES_NOT_HAVE_ENOUGH_POINTS));
            }
            long promotionId = insertPromotionAndUpdatePoints(
                    PromotionConfiguration.fromJSONObject(new JSONObject(promotionConfiguration.getObject())),
                    CompanyClientMapping.fromJSONObject(new JSONObject(serviceResult.getObject())));
            return new ServiceResult<>(true, getServiceMessage(Message.PROMOTION_APPLIED), promotionId);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return new ServiceResult<>(false, getServiceMessage(Message.COMMON_USER_ERROR), null);
        }
    }

    private long insertPromotionAndUpdatePoints(PromotionConfiguration promotionConfiguration,
                                                CompanyClientMapping companyClientMapping) throws Exception {

        if (promotionConfiguration == null) {
            throw new IllegalArgumentException("promotionConfiguration doesn't exist");
        }
        Promotion promotion = new Promotion();
        promotion.setCompanyId(promotionConfiguration.getCompanyId());
        promotion.setClientId(companyClientMapping.getClient().getClientId());
        promotion.setDescription(promotionConfiguration.getDescription());
        promotion.setUsedPoints(promotionConfiguration.getRequiredPoints());
        promotion.setDate(DateUtil.dateNow());

        long promotionId = _promotionRepository.insert(promotion);
        companyClientMapping.setPoints(companyClientMapping.getPoints() - promotion.getUsedPoints());
        _companyClientMappingRepository.updatePoints(companyClientMapping);
        return promotionId;
    }
}
