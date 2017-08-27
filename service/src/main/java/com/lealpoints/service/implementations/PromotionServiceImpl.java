package com.lealpoints.service.implementations;

import com.lealpoints.context.ThreadContextService;
import com.lealpoints.db.queryagent.QueryAgent;
import com.lealpoints.i18n.Message;
import com.lealpoints.model.Client;
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
            final QueryAgent queryAgent = getQueryAgent();
            final xyz.greatapp.libs.service.ServiceResult client = _clientRepository.getByPhone(promotionApplying.getPhoneNumber());
            if (client.getObject().equals("{}")) {
                return new ServiceResult<>(false, getServiceMessage(Message.PHONE_NUMBER_DOES_NOT_EXIST));
            }

            CompanyClientMapping companyClientMapping = _companyClientMappingRepository.getByCompanyIdClientId(
                    promotionApplying.getCompanyId(), new JSONObject(client.getObject()).getLong("client_id"));
            PromotionConfiguration promotionConfiguration = _promotionConfigurationRepository.getById(
                    promotionApplying.getPromotionConfigurationId());

            if (companyClientMapping.getPoints() < promotionConfiguration.getRequiredPoints()) {
                return new ServiceResult<>(false, getServiceMessage(Message.CLIENT_DOES_NOT_HAVE_ENOUGH_POINTS));
            }
            queryAgent.beginTransaction();
            long promotionId = insertPromotionAndUpdatePoints(promotionConfiguration, companyClientMapping);
            queryAgent.commitTransaction();
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

        if (companyClientMapping == null) {
            throw new IllegalArgumentException("CompanyClientMapping doesn't exist.");
        }
        companyClientMapping.setPoints(companyClientMapping.getPoints() - promotion.getUsedPoints());
        _companyClientMappingRepository.updatePoints(companyClientMapping);
        return promotionId;
    }
}
