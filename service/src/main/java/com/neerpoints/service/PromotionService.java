package com.neerpoints.service;

import com.neerpoints.context.ThreadContextService;
import com.neerpoints.db.QueryAgent;
import com.neerpoints.model.Client;
import com.neerpoints.model.CompanyClientMapping;
import com.neerpoints.model.Promotion;
import com.neerpoints.model.PromotionConfiguration;
import com.neerpoints.repository.ClientRepository;
import com.neerpoints.repository.CompanyClientMappingRepository;
import com.neerpoints.repository.PromotionConfigurationRepository;
import com.neerpoints.repository.PromotionRepository;
import com.neerpoints.service.model.PromotionApplying;
import com.neerpoints.service.model.ServiceResult;
import com.neerpoints.util.DateUtil;
import com.neerpoints.util.Translations;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PromotionService extends BaseService {
    private static final Logger logger = LogManager.getLogger(PromotionService.class.getName());
    private final PromotionRepository _promotionRepository;
    private final PromotionConfigurationRepository _promotionConfigurationRepository;
    private final CompanyClientMappingRepository _companyClientMappingRepository;
    private final ClientRepository _clientRepository;
    private final ThreadContextService _threadContextService;

    @Autowired
    public PromotionService(PromotionRepository promotionRepository, PromotionConfigurationRepository promotionConfigurationRepository,
        CompanyClientMappingRepository companyClientMappingRepository, ClientRepository clientRepository, ThreadContextService threadContextService,
        Translations translations) {
        super(translations, threadContextService);
        _promotionRepository = promotionRepository;
        _promotionConfigurationRepository = promotionConfigurationRepository;
        _companyClientMappingRepository = companyClientMappingRepository;
        _clientRepository = clientRepository;
        _threadContextService = threadContextService;
    }

    public ServiceResult<Long> applyPromotion(PromotionApplying promotionApplying) {
        try {
            final QueryAgent queryAgent = _threadContextService.getQueryAgent();
            final Client client = _clientRepository.getByPhone(promotionApplying.getPhone());
            if (client == null) {
                throw new IllegalArgumentException("Client doesn't exist.");
            }
            queryAgent.beginTransaction();
            long promotionId = insertPromotionAndUpdatePoints(promotionApplying, client);
            queryAgent.commitTransaction();
            return new ServiceResult<>(true, getTranslation(Translations.Message.PROMOTION_APPLIED), promotionId);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return new ServiceResult<>(false, getTranslation(Translations.Message.COMMON_USER_ERROR), null);
        }
    }

    private long insertPromotionAndUpdatePoints(PromotionApplying promotionApplying, Client client) throws Exception {
        PromotionConfiguration promotionConfiguration = _promotionConfigurationRepository.getById(promotionApplying.getPromotionConfigurationId());
        if (promotionConfiguration == null) {
            throw new IllegalArgumentException("promotionConfiguration doesn't exist");
        }
        Promotion promotion = new Promotion();
        promotion.setCompanyId(promotionApplying.getCompanyId());
        promotion.setClientId(client.getClientId());
        promotion.setDescription(promotionConfiguration.getDescription());
        promotion.setUsedPoints(promotionConfiguration.getRequiredPoints());
        promotion.setDate(DateUtil.dateNow());
        long promotionId = _promotionRepository.insert(promotion);

        //Updating points in client table
        CompanyClientMapping companyClientMapping =
            _companyClientMappingRepository.getByCompanyIdClientId(promotionApplying.getCompanyId(), client.getClientId());
        if (companyClientMapping == null) {
            throw new IllegalArgumentException("CompanyClientMapping doesn't exist.");
        }
        companyClientMapping.setPoints(companyClientMapping.getPoints() - promotion.getUsedPoints());
        _companyClientMappingRepository.updatePoints(companyClientMapping);
        return promotionId;
    }
}
