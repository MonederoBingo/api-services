package com.lealpoints.service.implementations;

import com.lealpoints.i18n.Message;
import com.lealpoints.model.PromotionConfiguration;
import com.lealpoints.repository.ClientRepository;
import com.lealpoints.repository.CompanyClientMappingRepository;
import com.lealpoints.repository.PromotionConfigurationRepository;
import com.lealpoints.service.PromotionConfigurationService;
import com.lealpoints.service.response.ServiceMessage;
import com.lealpoints.service.response.ServiceResult;
import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Predicate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.greatapp.libs.service.context.ThreadContextService;

import java.util.ArrayList;
import java.util.List;

@Component
public class PromotionConfigurationServiceImpl extends BaseServiceImpl implements PromotionConfigurationService {
    private static final Logger logger = LogManager.getLogger(PromotionConfigurationServiceImpl.class.getName());
    private final PromotionConfigurationRepository _promotionConfigurationRepository;
    private final CompanyClientMappingRepository _companyClientMappingRepository;
    private final ClientRepository _clientRepository;

    @Autowired
    public PromotionConfigurationServiceImpl(PromotionConfigurationRepository promotionConfigurationRepository,
                                             CompanyClientMappingRepository companyClientMappingRepository, ClientRepository clientRepository,
                                             ThreadContextService threadContextService) {
        super(threadContextService);
        _promotionConfigurationRepository = promotionConfigurationRepository;
        _companyClientMappingRepository = companyClientMappingRepository;
        _clientRepository = clientRepository;
    }

    public xyz.greatapp.libs.service.ServiceResult getByCompanyId(long companyId) {
        try {
            return _promotionConfigurationRepository.getByCompanyId(companyId);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return new xyz.greatapp.libs.service.ServiceResult(false, "", null);
        }
    }

    public ServiceResult<Long> insert(PromotionConfiguration promotionConfiguration) {
        try {
            final long promotionConfigurationId = _promotionConfigurationRepository.insert(promotionConfiguration);
            return new ServiceResult<>(true, getServiceMessage(Message.PROMOTION_SUCCESSFULLY_ADDED), promotionConfigurationId);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return new ServiceResult<>(false, getServiceMessage(Message.COMMON_USER_ERROR), null);
        }
    }

    public xyz.greatapp.libs.service.ServiceResult getByCompanyIdRequiredPoints(long companyId, final String phone) {
        try {
            final xyz.greatapp.libs.service.ServiceResult client = _clientRepository.getByPhone(phone);
            if (client.getObject().equals("{}")) {
                return new xyz.greatapp.libs.service.ServiceResult(false, Message.PHONE_NUMBER_DOES_NOT_EXIST.name());
            }
            final xyz.greatapp.libs.service.ServiceResult serviceResult =
                    _companyClientMappingRepository.getByCompanyIdClientId(companyId, new JSONObject(client.getObject()).getLong("client_id"));
            if (serviceResult.getObject().equals("{}")) {
                return new xyz.greatapp.libs.service.ServiceResult(false, Message.PHONE_NUMBER_DOES_NOT_EXIST.name());
            }
            final xyz.greatapp.libs.service.ServiceResult promotionConfigurations = _promotionConfigurationRepository.getByCompanyId(companyId);
            JSONArray jsonArray = new JSONArray(promotionConfigurations.getObject());
            List<PromotionConfiguration> configs = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                configs.add(PromotionConfiguration.fromJSONObject(jsonArray.getJSONObject(i)));
            }
            final List<PromotionConfiguration> resultPromotionConfigurations =
                    (List<PromotionConfiguration>) CollectionUtils.select(configs, new Predicate<PromotionConfiguration>() {
                        @Override
                        public boolean evaluate(PromotionConfiguration promotionConfiguration) {
                            return promotionConfiguration.getRequiredPoints() <= new JSONObject(serviceResult.getObject()).getDouble("points");
                        }
                    });
            String message = "";
            if (resultPromotionConfigurations.isEmpty()) {
                message = Message.CLIENT_DOES_NOT_HAVE_AVAILABLE_PROMOTIONS.name();
            }
            JSONArray result = new JSONArray();
            for (PromotionConfiguration resultPromotionConfiguration : resultPromotionConfigurations) {
                result.put(resultPromotionConfiguration.toJSONObject());
            }
            return new xyz.greatapp.libs.service.ServiceResult(true, message, result.toString());
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return new xyz.greatapp.libs.service.ServiceResult(false, Message.COMMON_USER_ERROR.name(), null);
        }
    }

    public ServiceResult<Boolean> deletePromotionConfiguration(long promotionConfigurationId) {
        try {
            final int deletedRows = _promotionConfigurationRepository.delete(promotionConfigurationId);
            if (deletedRows > 0) {
                ServiceMessage message = getServiceMessage(Message.THE_PROMOTION_WAS_DELETED);
                return new ServiceResult<>(true, message, true);
            } else {
                ServiceMessage message = getServiceMessage(Message.THE_PROMOTION_COULD_NOT_BE_DELETED);
                return new ServiceResult<>(false, message, false);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return new ServiceResult<>(false, getServiceMessage(Message.COMMON_USER_ERROR), null);
        }
    }
}
