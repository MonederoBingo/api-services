package com.neerpoints.service;

import com.neerpoints.context.ThreadContextService;
import com.neerpoints.repository.CompanyUserRepository;
import com.neerpoints.service.model.ServiceResult;
import com.neerpoints.util.Translations;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationService extends BaseService {
    private static final Logger logger = LogManager.getLogger(AuthenticationService.class.getName());

    private final CompanyUserRepository _companyUserRepository;

    @Autowired
    public AuthenticationService(ThreadContextService threadContextService, Translations translations, CompanyUserRepository companyUserRepository) {
        super(translations, threadContextService);
        _companyUserRepository = companyUserRepository;
    }

    public ServiceResult isValidApiKey(String companyUserId, String apiKey) {
        try {
            return new ServiceResult(_companyUserRepository.getByCompanyUserIdApiKey(companyUserId, apiKey) != null, "");
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return new ServiceResult(false, getTranslation(Translations.Message.COMMON_USER_ERROR));
        }
    }
}
