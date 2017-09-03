package com.lealpoints.service.implementations;

import com.lealpoints.i18n.Message;
import com.lealpoints.model.Company;
import com.lealpoints.model.CompanyUser;
import com.lealpoints.model.PromotionConfiguration;
import com.lealpoints.repository.ClientRepository;
import com.lealpoints.repository.CompanyRepository;
import com.lealpoints.repository.CompanyUserRepository;
import com.lealpoints.repository.PromotionConfigurationRepository;
import com.lealpoints.service.CompanyService;
import com.lealpoints.service.NotificationService;
import com.lealpoints.service.PointsConfigurationService;
import com.lealpoints.service.model.CompanyRegistration;
import com.lealpoints.service.model.ValidationResult;
import com.lealpoints.service.response.ServiceMessage;
import com.lealpoints.service.response.ServiceResult;
import com.lealpoints.service.util.ServiceUtil;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.greatapp.libs.service.context.ThreadContextService;

import java.io.File;
import java.util.Date;
import java.util.List;

@Component
public class CompanyServiceImpl extends BaseServiceImpl implements CompanyService {
    private static final Logger logger = LogManager.getLogger(CompanyServiceImpl.class.getName());
    private final CompanyRepository _companyRepository;
    private final CompanyUserRepository _companyUserRepository;
    private final ClientRepository _clientRepository;
    private final PromotionConfigurationRepository _promotionConfigurationRepository;
    private final ServiceUtil _serviceUtil;
    private final NotificationService notificationService;
    private final PointsConfigurationService pointsConfigurationService;

    @Autowired
    public CompanyServiceImpl(CompanyRepository companyRepository, CompanyUserRepository companyUserRepository,
                              ClientRepository clientRepository, ThreadContextService threadContextService,
                              PromotionConfigurationRepository promotionConfigurationRepository, ServiceUtil serviceUtil,
                              NotificationService notificationService, PointsConfigurationService pointsConfigurationService) {
        super(threadContextService);
        _companyRepository = companyRepository;
        _companyUserRepository = companyUserRepository;
        _clientRepository = clientRepository;
        _promotionConfigurationRepository = promotionConfigurationRepository;
        _serviceUtil = serviceUtil;
        this.notificationService = notificationService;
        this.pointsConfigurationService = pointsConfigurationService;
    }

    public ServiceResult<String> register(CompanyRegistration companyRegistration) {
        try {
            ValidationResult validationResult = validateRegistration(companyRegistration);
            if (validationResult.isValid()) {
                final String activationKey = _serviceUtil.generateActivationKey();
                long companyId = registerAndInitializeCompany(companyRegistration, activationKey);
                return createServiceResult(companyId, activationKey);
            } else {
                return new ServiceResult<>(false, validationResult.getServiceMessage());
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return new ServiceResult<>(false, getServiceMessage(Message.COMMON_USER_ERROR), null);
        }
    }

    private ServiceResult<String> createServiceResult(long companyId, String activationKey) {
        final ServiceMessage serviceMessage = getServiceMessage(Message.WE_HAVE_SENT_YOU_AND_ACTIVATION_LINK);
        final ServiceResult<String> serviceResult = new ServiceResult<>(true, serviceMessage, Long.toString(companyId));
        if (isFunctionalTestEnvironment()) {
            serviceResult.setExtraInfo(activationKey);
        }
        return serviceResult;
    }

    public xyz.greatapp.libs.service.ServiceResult getPointsInCompanyByPhone(String phone) {
        try {
            xyz.greatapp.libs.service.ServiceResult serviceResult = _clientRepository.getByPhone(phone);
            return _companyRepository.getPointsInCompanyByClientId(new JSONObject(serviceResult.getObject()).getLong("client_id"));
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return new xyz.greatapp.libs.service.ServiceResult(false, "", null);
        }
    }

    public xyz.greatapp.libs.service.ServiceResult getByCompanyId(long companyId) {
        try {
            final xyz.greatapp.libs.service.ServiceResult company = _companyRepository.getByCompanyId(companyId);
            if (company != null) {
                JSONObject jsonObject = new JSONObject(company.getObject());
                jsonObject.put("url_image_logo", jsonObject.get("url_image_logo") + "?" + +new Date().getTime());
                return new xyz.greatapp.libs.service.ServiceResult(company.isSuccess(), company.getMessage(), jsonObject.toString());
            } else {
                logger.error("None company has the companyId: " + companyId);
                return new xyz.greatapp.libs.service.ServiceResult(false, "");
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return new xyz.greatapp.libs.service.ServiceResult(false, "", null);
        }
    }

    public ServiceResult<Boolean> updateLogo(List<FileItem> fileItems, long companyId) {
        try {
            if (fileItems.size() > 0) {
                final FileItem fileItem = fileItems.get(0);
                final String contentType = fileItem.getContentType();
                if (!isValidContentType(contentType)) {
                    return new ServiceResult<>(false, getServiceMessage(Message.INVALID_LOGO_FILE));
                }
                final String fileName = companyId + "." + getExtensionFromContentType(contentType);
                fileItem.write(new File(String.valueOf("images/prod/" + fileName)));
                _companyRepository.updateUrlImageLogo(companyId, fileName);
            } else {
                return new ServiceResult<>(false, getServiceMessage(Message.COULD_NOT_READ_FILE));
            }
            return new ServiceResult<>(true, getServiceMessage(Message.YOUR_LOGO_WAS_UPDATED));
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return new ServiceResult<>(false, getServiceMessage(Message.COMMON_USER_ERROR));
        }
    }

    public File getLogo(long companyId) {
        try {
            final xyz.greatapp.libs.service.ServiceResult company = _companyRepository.getByCompanyId(companyId);
            if (!"{}".equals(company.getObject())) {
                String urlImageLogo = new JSONObject(company.getObject()).get("url_image_logo").toString();
                if (StringUtils.isEmpty(urlImageLogo)) {
                    return new File("images/prod/" + "no_image.png");
                } else {
                    return new File("images/prod/" + urlImageLogo);
                }
            } else {
                logger.error("None company has the companyId: " + companyId);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return null;
        }
        return null;
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

    private long registerAndInitializeCompany(CompanyRegistration companyRegistration, String activationKey) throws Exception {
        long companyId = registerCompany(companyRegistration);
        registerPointsConfiguration(companyId);
        registerPromotionConfiguration(companyId);
        registerCompanyUser(companyRegistration, companyId, activationKey);
        notificationService.sendActivationEmail(companyRegistration.getEmail(), activationKey);
        return companyId;
    }

    private String registerCompanyUser(CompanyRegistration companyRegistration, long companyId, String activationKey) throws Exception {
        CompanyUser companyUser = new CompanyUser();
        companyUser.setCompanyId(companyId);
        companyUser.setName(companyRegistration.getUsername());
        companyUser.setPassword(companyRegistration.getPassword());
        companyUser.setEmail(companyRegistration.getEmail());
        setUserActivation(companyUser);
        companyUser.setActivationKey(activationKey);
        String language = companyRegistration.getLanguage();
        if (StringUtils.isNotEmpty(language)) {
            language = language.substring(0, 2);
        }
        companyUser.setLanguage(language);
        companyUser.setMustChangePassword(false);
        _companyUserRepository.insert(companyUser);
        return activationKey;
    }

    public void setUserActivation(CompanyUser companyUser) {
        if (isDevEnvironment()) {
            companyUser.setActive(true);
        } else {
            companyUser.setActive(false);
        }
    }

    private void registerPromotionConfiguration(long companyId) throws Exception {
        PromotionConfiguration promotionConfiguration = new PromotionConfiguration();
        promotionConfiguration.setCompanyId(companyId);
        promotionConfiguration.setRequiredPoints(1000);
        promotionConfiguration.setDescription(getServiceMessage(Message.DEFAULT_PROMOTION_MESSAGE).getMessage());
        _promotionConfigurationRepository.insert(promotionConfiguration);
    }

    void registerPointsConfiguration(long companyId) throws Exception {
        pointsConfigurationService.registerPointsConfiguration(companyId);
    }

    private long registerCompany(CompanyRegistration companyRegistration) throws Exception {
        Company company = new Company();
        company.setName(companyRegistration.getCompanyName());
        company.setUrlImageLogo(companyRegistration.getUrlImageLogo());
        return _companyRepository.insert(company);
    }

    private ValidationResult validateRegistration(CompanyRegistration companyRegistration) throws Exception {
        // Validate company name
        if (StringUtils.isEmpty(companyRegistration.getCompanyName())) {
            return new ValidationResult(false, getServiceMessage(Message.COMPANY_NAME_IS_EMPTY));
        }
        // Validate user password
        if (companyRegistration.getPassword() == null || companyRegistration.getPassword().length() < 6) {
            return new ValidationResult(false, getServiceMessage(Message.PASSWORD_MUST_HAVE_AT_LEAST_6_CHARACTERS));
        }
        if (!companyRegistration.getPassword().equals(companyRegistration.getPasswordConfirmation())) {
            return new ValidationResult(false, getServiceMessage(Message.PASSWORD_AND_CONFIRMATION_ARE_DIFFERENT));
        }
        // Validate user email
        if (StringUtils.isEmpty(companyRegistration.getEmail())) {
            return new ValidationResult(false, getServiceMessage(Message.EMAIL_IS_EMPTY));
        }
        if (!EmailValidator.getInstance().isValid(companyRegistration.getEmail())) {
            return new ValidationResult(false, getServiceMessage(Message.EMAIL_IS_INVALID));
        }
        xyz.greatapp.libs.service.ServiceResult serviceResult = _companyUserRepository.getByEmail(companyRegistration.getEmail());
        if (!"{}".equals(serviceResult.getObject())) {
            return new ValidationResult(false, getServiceMessage(Message.EMAIL_ALREADY_EXISTS));
        }
        return new ValidationResult(true, ServiceMessage.EMPTY);
    }
}
