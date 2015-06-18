package com.neerpoints.service;

import java.util.List;
import com.neerpoints.context.ThreadContextService;
import com.neerpoints.model.Client;
import com.neerpoints.model.ClientPoints;
import com.neerpoints.model.CompanyClientMapping;
import com.neerpoints.repository.ClientRepository;
import com.neerpoints.repository.CompanyClientMappingRepository;
import com.neerpoints.service.model.ClientRegistration;
import com.neerpoints.service.model.ServiceResult;
import com.neerpoints.service.model.ValidationResult;
import com.neerpoints.util.Translations;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClientService extends BaseService {
    private static final Logger logger = LogManager.getLogger(ClientService.class.getName());
    private final ClientRepository _clientRepository;
    private final CompanyClientMappingRepository _companyClientMappingRepository;
    private final ThreadContextService _threadContextService;

    @Autowired
    public ClientService(ClientRepository clientRepository, CompanyClientMappingRepository companyClientMappingRepository,
        ThreadContextService threadContextService, Translations translations) {
        super(translations, threadContextService);
        _clientRepository = clientRepository;
        _companyClientMappingRepository = companyClientMappingRepository;
        _threadContextService = threadContextService;
    }

    public ServiceResult<Long> register(ClientRegistration clientRegistration) {
        try {
            ValidationResult validationResult = validateRegistration(clientRegistration);
            if (validationResult.isSuccess()) {
                _threadContextService.getQueryAgent().beginTransaction();
                Client client = registerClientAndCompanyMapping(clientRegistration);
                _threadContextService.getQueryAgent().commitTransaction();
                return new ServiceResult<>(true, getTranslation(Translations.Message.CLIENT_REGISTERED_SUCCESSFULLY), client.getClientId());
            } else {
                return new ServiceResult<>(false, validationResult.getMessage());
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return new ServiceResult<>(false, getTranslation(Translations.Message.COMMON_USER_ERROR), null);
        }
    }

    public ServiceResult<List<ClientPoints>> getByCompanyId(long companyId) {
        try {
            List<ClientPoints> clientPointsList = _clientRepository.getByCompanyId(companyId);
            return new ServiceResult<>(true, "", clientPointsList);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return new ServiceResult<>(false, getTranslation(Translations.Message.COMMON_USER_ERROR), null);
        }
    }

    public ServiceResult<ClientPoints> getByCompanyIdPhone(long companyId, String phone) {
        try {
            ClientPoints clientPoints = _clientRepository.getByCompanyIdPhone(companyId, phone);
            return new ServiceResult<>(true, "", clientPoints);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return new ServiceResult<>(false, getTranslation(Translations.Message.COMMON_USER_ERROR), null);
        }
    }

    private Client registerClientAndCompanyMapping(ClientRegistration clientRegistration) throws Exception {
        //Client could exist for other companies
        Client client = _clientRepository.insertIfDoesNotExist(clientRegistration.getPhone());

        CompanyClientMapping companyClientMapping = new CompanyClientMapping();
        companyClientMapping.setCompanyId(clientRegistration.getCompanyId());
        companyClientMapping.setClientId(client.getClientId());
        _companyClientMappingRepository.insert(companyClientMapping);
        return client;
    }

    private ValidationResult validateRegistration(ClientRegistration clientRegistration) throws Exception {
        if (clientRegistration.getPhone().length() != 10) {
            return new ValidationResult(false, getTranslation(Translations.Message.PHONE_MUST_HAVE_10_DIGITS));
        }
        Client client = _clientRepository.getByPhone(clientRegistration.getPhone());
        if (client != null) {
            CompanyClientMapping companyClientMapping =
                _companyClientMappingRepository.getByCompanyIdClientId(clientRegistration.getCompanyId(), client.getClientId());
            if (companyClientMapping != null) {
                return new ValidationResult(false, getTranslation(Translations.Message.THE_CLIENT_ALREADY_EXISTS));
            }
        }
        return new ValidationResult(true, "");
    }
}
