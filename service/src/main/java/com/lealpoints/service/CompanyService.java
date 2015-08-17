package com.lealpoints.service;

import javax.mail.MessagingException;
import java.io.File;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import com.lealpoints.context.ThreadContext;
import com.lealpoints.context.ThreadContextService;
import com.lealpoints.model.Client;
import com.lealpoints.model.Company;
import com.lealpoints.model.CompanyUser;
import com.lealpoints.model.NotificationEmail;
import com.lealpoints.model.PointsConfiguration;
import com.lealpoints.model.PointsInCompany;
import com.lealpoints.model.PromotionConfiguration;
import com.lealpoints.repository.ClientRepository;
import com.lealpoints.repository.CompanyClientMappingRepository;
import com.lealpoints.repository.CompanyRepository;
import com.lealpoints.repository.CompanyUserRepository;
import com.lealpoints.repository.PointsConfigurationRepository;
import com.lealpoints.repository.PromotionConfigurationRepository;
import com.lealpoints.service.annotation.OnlyProduction;
import com.lealpoints.service.base.BaseService;
import com.lealpoints.service.model.CompanyRegistration;
import com.lealpoints.service.model.ServiceResult;
import com.lealpoints.service.model.ValidationResult;
import com.lealpoints.util.EmailUtil;
import com.lealpoints.util.Translations;
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
    private final SMSService _smsService;
    private final CompanyClientMappingRepository _companyClientMappingRepository;
    private final PromotionConfigurationRepository _promotionConfigurationRepository;

    @Autowired
    public CompanyService(CompanyRepository companyRepository, CompanyUserRepository companyUserRepository,
        PointsConfigurationRepository pointsConfigurationRepository, ClientRepository clientRepository, ThreadContextService threadContextService,
        Translations translations, SMSService smsService, CompanyClientMappingRepository companyClientMappingRepository,
        PromotionConfigurationRepository promotionConfigurationRepository) {
        super(translations, threadContextService);
        _companyRepository = companyRepository;
        _companyUserRepository = companyUserRepository;
        _pointsConfigurationRepository = pointsConfigurationRepository;
        _clientRepository = clientRepository;
        _threadContextService = threadContextService;
        _smsService = smsService;
        _companyClientMappingRepository = companyClientMappingRepository;
        _promotionConfigurationRepository = promotionConfigurationRepository;
    }

    public ServiceResult<Long> register(CompanyRegistration companyRegistration) {
        try {
            ValidationResult validationResult = validateRegistration(companyRegistration);
            if (validationResult.isValid()) {
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
            if (company != null) {
                company.setUrlImageLogo(company.getUrlImageLogo() + "?" + new Date().getTime());
                return new ServiceResult<>(true, "", company);
            } else{
                logger.error("None company has the companyId: " + companyId);
                return new ServiceResult<>(false, getTranslation(Translations.Message.COMMON_USER_ERROR));
            }
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
                fileItem.write(new File(String.valueOf(getThreadContext().getEnvironment().getImageDir() + fileName)));
                _companyRepository.updateUrlImageLogo(companyId, fileName);
            } else {
                return new ServiceResult<>(false, getTranslation(Translations.Message.COULD_NOT_READ_FILE));
            }
            return new ServiceResult<>(true, getTranslation(Translations.Message.YOUR_LOGO_WAS_UPDATED));
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return new ServiceResult<>(false, getTranslation(Translations.Message.COMMON_USER_ERROR));
        }
    }

    @OnlyProduction
    public ServiceResult sendMobileAppAdMessage(long companyId, String phone) {
        if (isProdEnvironment()) {
            try {
                final Company company = _companyRepository.getByCompanyId(companyId);
                long clientId = _clientRepository.getByPhone(phone).getClientId();
                final double points = _companyClientMappingRepository.getByCompanyIdClientId(companyId, clientId).getPoints();
                if (company != null) {
                    final String smsMessage = getSMSMessage(company.getName(), points);
                    System.out.println(smsMessage);
                    _smsService.sendSMSMessage(phone, smsMessage);
                    _clientRepository.updateCanReceivePromoSms(clientId, false);
                } else {
                    logger.error("None company has the companyId: " + companyId);
                    return new ServiceResult<>(false, getTranslation(Translations.Message.COMMON_USER_ERROR));
                }
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
                return new ServiceResult(false, getTranslation(Translations.Message.MOBILE_APP_AD_MESSAGE_WAS_NOT_SENT_SUCCESSFULLY));
            }
            return new ServiceResult(true, getTranslation(Translations.Message.MOBILE_APP_AD_MESSAGE_SENT_SUCCESSFULLY));
        }
        return new ServiceResult(false, getTranslation(Translations.Message.COMMON_USER_ERROR));
    }

    String getSMSMessage(String companyName, double points) {
        final String translation = getTranslation(Translations.Message.MOBILE_APP_AD_MESSAGE);
        int SMS_MESSAGE_MAX_CHAR = 160;
        final int formattedMessageLength = String.format(translation, points, "", "http://tinyurl.com/og2b56y").length();
        if (formattedMessageLength >= SMS_MESSAGE_MAX_CHAR) {
            throw new IllegalArgumentException("Message length must be less than " + SMS_MESSAGE_MAX_CHAR + " in: " + translation);
        }
        final int maxCompanyNameLength = SMS_MESSAGE_MAX_CHAR - formattedMessageLength;
        String trimmedCompanyName = "";
        if (maxCompanyNameLength > 0) {
            trimmedCompanyName = StringUtils.abbreviate(companyName, Math.max(Math.min(maxCompanyNameLength, companyName.length()), 4));
        }
        return String.format(translation, new DecimalFormat("#.#").format(points), trimmedCompanyName, "http://tinyurl.com/og2b56y");
    }

    public File getLogo(long companyId) {
        try {
            final Company company = _companyRepository.getByCompanyId(companyId);
            if (company != null) {
                return new File(getThreadContext().getEnvironment().getImageDir() + company.getUrlImageLogo());
            } else {
                logger.error("None company has the companyId: " + companyId);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return null;
        }
        return null;
    }

    private ThreadContext getThreadContext() {
        return _threadContextService.getThreadContext();
    }

    private boolean isValidContentType(String contentType) {
        return contentType.equalsIgnoreCase("image/jpeg") ||
            contentType.equalsIgnoreCase("image/png") ||
            contentType.equalsIgnoreCase("image/gif");
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
        pointsConfiguration.setPointsToEarn(1);
        pointsConfiguration.setRequiredAmount(1);
        _pointsConfigurationRepository.insert(pointsConfiguration);

        PromotionConfiguration promotionConfiguration = new PromotionConfiguration();
        promotionConfiguration.setCompanyId(companyId);
        promotionConfiguration.setRequiredPoints(1000);
        promotionConfiguration.setDescription(getTranslation(Translations.Message.DEFAULT_PROMOTION_MESSAGE));
        _promotionConfigurationRepository.insert(promotionConfiguration);

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
        final String activationUrl = getEnvironment().getClientUrl() + "activate?key=" + activationKey;
        notificationEmail.setBody(getTranslation(Translations.Message.ACTIVATION_EMAIL_BODY) + "\n\n" + activationUrl);
        notificationEmail.setEmailTo(email);
        EmailUtil.sendEmail(notificationEmail);
    }

    private ValidationResult validateRegistration(CompanyRegistration companyRegistration) throws Exception {
        //Validate user password
        if (companyRegistration.getPassword() == null || companyRegistration.getPassword().length() < 6) {
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