package com.lealpoints.service.implementations;

import com.lealpoints.context.ThreadContextService;
import com.lealpoints.i18n.Message;
import com.lealpoints.model.Client;
import com.lealpoints.model.CompanyClientMapping;
import com.lealpoints.repository.ClientRepository;
import com.lealpoints.repository.CompanyClientMappingRepository;
import com.lealpoints.service.ClientService;
import com.lealpoints.service.model.ClientRegistration;
import com.lealpoints.service.model.ServiceResult;
import com.lealpoints.service.model.ValidationResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ClientServiceImpl extends BaseServiceImpl implements ClientService {
    private static final Logger logger = LogManager.getLogger(ClientServiceImpl.class.getName());
    private final ClientRepository _clientRepository;
    private final CompanyClientMappingRepository _companyClientMappingRepository;
    private final PhoneValidatorServiceImpl _phoneValidatorService;

    @Autowired
    public ClientServiceImpl(ClientRepository clientRepository, CompanyClientMappingRepository companyClientMappingRepository,
                             ThreadContextService threadContextService, PhoneValidatorServiceImpl phoneValidatorService) {
        super(threadContextService);
        _clientRepository = clientRepository;
        _companyClientMappingRepository = companyClientMappingRepository;
        _phoneValidatorService = phoneValidatorService;
    }

    public ServiceResult<Long> register(ClientRegistration clientRegistration) {
        try {
            ValidationResult validationResult = validateRegistration(clientRegistration);
            if (validationResult.isValid()) {
                getThreadContextService().getQueryAgent().beginTransaction();
                Client client = registerClientAndCompanyMapping(clientRegistration);
                getThreadContextService().getQueryAgent().commitTransaction();
                return new ServiceResult<>(true, getTranslation(Message.CLIENT_REGISTERED_SUCCESSFULLY), client.getClientId());
            } else {
                return new ServiceResult<>(false, validationResult.getMessage());
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return new ServiceResult<>(false, getTranslation(Message.COMMON_USER_ERROR), null);
        }
    }

    private Client registerClientAndCompanyMapping(ClientRegistration clientRegistration) throws Exception {
        //Client could exist for other companies
        Client client = _clientRepository.insertIfDoesNotExist(clientRegistration.getPhone(), true);
        CompanyClientMapping companyClientMapping = new CompanyClientMapping();
        companyClientMapping.setCompanyId(clientRegistration.getCompanyId());
        companyClientMapping.setClient(client);
        _companyClientMappingRepository.insert(companyClientMapping);
        return client;
    }

    public ServiceResult<List<CompanyClientMapping>> getByCompanyId(long companyId) {
        try {
            List<CompanyClientMapping> clientPointsList = _clientRepository.getByCompanyId(companyId);
            return new ServiceResult<>(true, "", clientPointsList);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return new ServiceResult<>(false, getTranslation(Message.COMMON_USER_ERROR), null);
        }
    }

    public ServiceResult<CompanyClientMapping> getByCompanyIdPhone(long companyId, String phone) {
        try {
            CompanyClientMapping clientPoints = _clientRepository.getByCompanyIdPhone(companyId, phone);
            return new ServiceResult<>(true, "", clientPoints);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return new ServiceResult<>(false, getTranslation(Message.COMMON_USER_ERROR), null);
        }
    }

    private ValidationResult validateRegistration(ClientRegistration clientRegistration) throws Exception {
        final ValidationResult phoneValidation = _phoneValidatorService.validate(clientRegistration.getPhone());
        if (phoneValidation.isInvalid()) {
            return phoneValidation;
        }
        Client client = _clientRepository.getByPhone(clientRegistration.getPhone());
        if (client != null) {
            CompanyClientMapping companyClientMapping =
                _companyClientMappingRepository.getByCompanyIdClientId(clientRegistration.getCompanyId(), client.getClientId());
            if (companyClientMapping != null) {
                return new ValidationResult(false, getTranslation(Message.THE_CLIENT_ALREADY_EXISTS));
            }
        }
        return new ValidationResult(true);
    }
}
