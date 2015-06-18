package com.neerpoints.service;

import javax.mail.MessagingException;
import java.io.File;
import java.util.Date;
import java.util.List;
import com.neerpoints.context.ThreadContext;
import com.neerpoints.context.ThreadContextService;
import com.neerpoints.model.Client;
import com.neerpoints.model.Company;
import com.neerpoints.model.CompanyUser;
import com.neerpoints.model.NotificationEmail;
import com.neerpoints.model.PointsConfiguration;
import com.neerpoints.model.PointsInCompany;
import com.neerpoints.repository.ClientRepository;
import com.neerpoints.repository.CompanyRepository;
import com.neerpoints.repository.CompanyUserRepository;
import com.neerpoints.repository.PointsConfigurationRepository;
import com.neerpoints.service.model.CompanyRegistration;
import com.neerpoints.service.model.ServiceResult;
import com.neerpoints.service.model.ValidationResult;
import com.neerpoints.util.EmailUtil;
import com.neerpoints.util.Translations;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CompanyService extends BaseService {
    private static final Logger logger = LogManager.getLogger(CompanyService.class.getName());
    private final CompanyRepository _companyRepository;
    private final CompanyUserRepository _companyUserRepository;
    private final PointsConfigurationRepository _pointsConfigurationRepository;
    private final ClientRepository _clientRepository;
    private final ThreadContextService _threadContextService;

    @Autowired
    public CompanyService(CompanyRepository companyRepository, CompanyUserRepository companyUserRepository,
        PointsConfigurationRepository pointsConfigurationRepository, ClientRepository clientRepository, ThreadContextService threadContextService,
        Translations translations) {
        super(translations, threadContextService);
        _companyRepository = companyRepository;
        _companyUserRepository = companyUserRepository;
        _pointsConfigurationRepository = pointsConfigurationRepository;
        _clientRepository = clientRepository;
        _threadContextService = threadContextService;
    }

    public ServiceResult<Long> register(CompanyRegistration companyRegistration) {
        try {
            ValidationResult validationResult = validateRegistration(companyRegistration);
            if (validationResult.isSuccess()) {
                _threadContextService.getQueryAgent().beginTransaction();
                long companyId = registerAndInitializeCompany(companyRegistration);
                _threadContextService.getQueryAgent().commitTransaction();
                return new ServiceResult<>(true, getTranslation(Translations.Message.WE_HAVE_SENT_YOU_AND_ACTIVATION_LINK), companyId);
            } else {
                return new ServiceResult<>(false, validationResult.getMessage());
            }

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return new ServiceResult<>(false, getTranslation(Translations.Message.COMMON_USER_ERROR), null);
        }
    }

    public ServiceResult<List<PointsInCompany>> getPointsInCompanyByPhone(String phone) {
        try {
            Client client = _clientRepository.getByPhone(phone);
            List<PointsInCompany> companies = _companyRepository.getPointsInCompanyByClientId(client.getClientId());
            return new ServiceResult<>(true, "companies", companies);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return new ServiceResult<>(false, getTranslation(Translations.Message.COMMON_USER_ERROR), null);
        }
    }

    public ServiceResult<Company> getByCompanyId(long companyId) {
        try {
            final Company company = _companyRepository.getByCompanyId(companyId);
            company.setUrlImageLogo(company.getUrlImageLogo() + "?" + new Date().getTime());
            return new ServiceResult<>(true, "", company);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return new ServiceResult<>(false, getTranslation(Translations.Message.COMMON_USER_ERROR), null);
        }
    }

    public ServiceResult<Boolean> updateLogo(List<FileItem> fileItems, long companyId) {
        try {
            if (fileItems.size() > 0) {
                final FileItem fileItem = fileItems.get(0);
                final String contentType = fileItem.getContentType();
                if (!isValidContentType(contentType)) {
                    return new ServiceResult<>(false, getTranslation(Translations.Message.INVALID_LOGO_FILE));
                }
                final String fileName = companyId + "." + getExtensionFromContentType(contentType);
                final String imageDir = getImageDir();
                fileItem.write(new File(String.valueOf(imageDir + fileName)));
                _companyRepository.updateUrlImageLogo(companyId, fileName);
            } else {
                return new ServiceResult<>(false, getTranslation(Translations.Message.COULD_NOT_READ_FILE));
            }
            return new ServiceResult<>(true, getTranslation(Translations.Message.YOUR_LOGO_WAS_UPDATED));
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return new ServiceResult<>(false, getTranslation(Translations.Message.COMMON_USER_ERROR), null);
        }
    }

    private String getImageDir() {
        return getThreadContext().isProdEnvironment() ? System.getenv("OPENSHIFT_DATA_DIR") + "images/" : "src/main/webapp/images/";
    }

    public File getLogo(long companyId) {
        try {
            final String imageDir = getImageDir();
            final Company company = _companyRepository.getByCompanyId(companyId);
            return new File(imageDir + company.getUrlImageLogo());
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return null;
        }
    }

    private ThreadContext getThreadContext() {
        return _threadContextService.getThreadContext();
    }

    private boolean isValidContentType(String contentType) {
        if (contentType.equalsIgnoreCase("image/jpeg") ||
            contentType.equalsIgnoreCase("image/png") ||
            contentType.equalsIgnoreCase("image/gif")) {
            return true;
        }
        return false;
    }

    private String getExtensionFromContentType(String contentType) {
        String extension = "";
        if (contentType.equalsIgnoreCase("image/jpeg")) {
            extension = "jpeg";
        } else if (contentType.equalsIgnoreCase("image/png")) {
            extension = "png";
        } else if (contentType.equalsIgnoreCase("image/gif")) {
            extension = "gif";
        }
        return extension;
    }

    private long registerAndInitializeCompany(CompanyRegistration companyRegistration) throws Exception {
        Company company = new Company();
        company.setName(companyRegistration.getCompanyName());
        company.setUrlImageLogo(companyRegistration.getUrlImageLogo());
        long companyId = _companyRepository.insert(company);

        PointsConfiguration pointsConfiguration = new PointsConfiguration();
        pointsConfiguration.setCompanyId(companyId);
        pointsConfiguration.setPointsToEarn(0);
        pointsConfiguration.setRequiredAmount(0);
        _pointsConfigurationRepository.insert(pointsConfiguration);

        final String activationKey = RandomStringUtils.random(60, true, true);
        CompanyUser companyUser = new CompanyUser();
        companyUser.setCompanyId(companyId);
        companyUser.setName(companyRegistration.getUserName());
        companyUser.setPassword(companyRegistration.getPassword());
        companyUser.setEmail(companyRegistration.getEmail());
        companyUser.setActive(false);
        companyUser.setActivationKey(activationKey);
        String language = companyRegistration.getLanguage();
        if (StringUtils.isNotEmpty(language)) {
            language = language.substring(0, 2);
        }
        companyUser.setLanguage(language);
        companyUser.setMustChangePassword(false);
        _companyUserRepository.insert(companyUser);

        sendActivationEmail(companyRegistration.getEmail(), activationKey);

        return companyId;
    }

    void sendActivationEmail(String email, String activationKey) throws MessagingException {
        NotificationEmail notificationEmail = new NotificationEmail();
        notificationEmail.setSubject(getTranslation(Translations.Message.ACTIVATION_EMAIL_SUBJECT));
        final String activationUrl = isProdEnvironment() ? "http://www.neerpoints.com/#/activate?key=" + activationKey :
            "http://localhost:8080/#/activate?key=" + activationKey;
        notificationEmail.setBody(getTranslation(Translations.Message.ACTIVATION_EMAIL_BODY) + "\n\n" + activationUrl);
        notificationEmail.setEmailTo(email);
        EmailUtil.sendEmail(notificationEmail);
    }

    private ValidationResult validateRegistration(CompanyRegistration companyRegistration) throws Exception {
        //Validate user password
        if (companyRegistration.getPassword().length() < 6) {
            return new ValidationResult(false, getTranslation(Translations.Message.PASSWORD_MUST_HAVE_AT_LEAST_6_CHARACTERS));
        }
        if (!companyRegistration.getPassword().equals(companyRegistration.getPasswordConfirmation())) {
            return new ValidationResult(false, getTranslation(Translations.Message.PASSWORD_AND_CONFIRMATION_ARE_DIFFERENT));
        }
        //Validate user email
        if (_companyUserRepository.getByEmail(companyRegistration.getEmail()) != null) {
            return new ValidationResult(false, getTranslation(Translations.Message.EMAIL_ALREADY_EXISTS));
        }
        return new ValidationResult(true, "");
    }
}