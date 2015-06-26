package com.neerpoints.service;

import javax.mail.MessagingException;
import com.neerpoints.context.ThreadContextService;
import com.neerpoints.model.CompanyUser;
import com.neerpoints.model.NotificationEmail;
import com.neerpoints.repository.CompanyRepository;
import com.neerpoints.repository.CompanyUserRepository;
import com.neerpoints.service.model.CompanyUserLogin;
import com.neerpoints.service.model.CompanyUserPasswordChanging;
import com.neerpoints.service.model.CompanyLoginResult;
import com.neerpoints.service.model.ServiceResult;
import com.neerpoints.service.model.ValidationResult;
import com.neerpoints.util.EmailUtil;
import com.neerpoints.util.Translations;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CompanyUserService extends BaseService {
    private static final Logger logger = LogManager.getLogger(CompanyUserService.class.getName());
    private final CompanyUserRepository _companyUserRepository;
    private final CompanyRepository _companyRepository;
    private final ThreadContextService _threadContextService;

    @Autowired
    public CompanyUserService(CompanyUserRepository companyUserRepository, ThreadContextService threadContextService, Translations translations,
        CompanyRepository companyRepository) {
        super(translations, threadContextService);
        _companyUserRepository = companyUserRepository;
        _threadContextService = threadContextService;
        _companyRepository = companyRepository;
    }

    public ServiceResult<CompanyLoginResult> loginUser(CompanyUserLogin companyUserLogin) {
        CompanyLoginResult loginResult = new CompanyLoginResult();
        try {
            if (companyUserLogin == null || companyUserLogin.getEmail() == null || companyUserLogin.getPassword() == null) {
                return null;
            }
            CompanyUser companyUser = _companyUserRepository.getByEmailAndPassword(companyUserLogin.getEmail(), companyUserLogin.getPassword());
            if (companyUser == null) {
                return new ServiceResult<>(false, getTranslation(Translations.Message.LOGIN_FAILED));
            }
            if (!companyUser.isActive()) {
                loginResult.setActive(false);
                return new ServiceResult<>(false, getTranslation(Translations.Message.YOUR_USER_IS_NOT_ACTIVE), loginResult);
            }
            String apiKey = RandomStringUtils.random(20, true, true) + "com";
            final int updatedRows = _companyUserRepository.updateApiKeyByEmail(companyUser.getEmail(), apiKey);
            if (updatedRows != 1) {
                logger.error("The company user api key could not be updated. updatedRows: " + updatedRows);
                return new ServiceResult<>(false, getTranslation(Translations.Message.COMMON_USER_ERROR));
            }
            loginResult.setEmail(companyUser.getEmail());
            loginResult.setMustChangePassword(companyUser.getMustChangePassword());
            loginResult.setActive(true);
            loginResult.setCompanyId(companyUser.getCompanyId());
            loginResult.setCompanyUserId(companyUser.getCompanyUserId());
            loginResult.setLanguage(companyUser.getLanguage());
            loginResult.setCompanyName(_companyRepository.getByCompanyId(companyUser.getCompanyId()).getName());
            loginResult.setApiKey(apiKey);
            return new ServiceResult<>(true, "", loginResult);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return new ServiceResult<>(false, getTranslation(Translations.Message.COMMON_USER_ERROR));
        }
    }

    public ServiceResult activateUser(String activationKey) throws Exception {
        try {
            _threadContextService.getQueryAgent().beginTransaction();
            int updatedRows = _companyUserRepository.updateActivateByActivationKey(activationKey);
            if (updatedRows > 0) {
                _companyUserRepository.clearActivationKey(activationKey);
                _threadContextService.getQueryAgent().commitTransaction();
                return new ServiceResult(true, getTranslation(Translations.Message.YOUR_USER_IS_ACTIVE_NOW));
            } else {
                _threadContextService.getQueryAgent().rollbackTransaction();
                return new ServiceResult(false, getTranslation(Translations.Message.COMMON_USER_ERROR));
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return new ServiceResult(false, getTranslation(Translations.Message.COMMON_USER_ERROR));
        }
    }

    public ServiceResult sendActivationEmail(String email) {
        try {
            final CompanyUser companyUser = _companyUserRepository.getByEmail(email);
            if (companyUser == null) {
                return new ServiceResult(false, getTranslation(Translations.Message.THIS_EMAIL_DOES_NOT_EXIST));
            }
            sendActivationEmail(email, companyUser.getActivationKey());
            return new ServiceResult(true, getTranslation(Translations.Message.WE_HAVE_SENT_YOU_AND_ACTIVATION_LINK));
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return new ServiceResult(false, getTranslation(Translations.Message.COMMON_USER_ERROR));
        }
    }

    public ServiceResult sendTempPasswordEmail(String email) {
        try {
            final CompanyUser companyUser = _companyUserRepository.getByEmail(email);
            if (companyUser == null) {
                return new ServiceResult(false, getTranslation(Translations.Message.THIS_EMAIL_DOES_NOT_EXIST));
            }
            final String tempPassword = RandomStringUtils.random(8, true, true);
            final int updatedRows = _companyUserRepository.updatePasswordByEmail(email, tempPassword, true);
            if (updatedRows > 0) {
                sendTempPasswordEmail(email, tempPassword);
            } else {
                return new ServiceResult(false, getTranslation(Translations.Message.COMMON_USER_ERROR));
            }
            return new ServiceResult(true, getTranslation(Translations.Message.WE_HAVE_SENT_YOU_A_NEW_PASSWORD_TO_YOUR_EMAIL));
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return new ServiceResult(false, getTranslation(Translations.Message.COMMON_USER_ERROR));
        }
    }

    public ServiceResult changePassword(CompanyUserPasswordChanging passwordChanging) {
        try {
            final ValidationResult validationResult = validatePassword(passwordChanging);
            if (validationResult.isSuccess()) {
                final int updatedRows =
                    _companyUserRepository.updatePasswordByEmail(passwordChanging.getEmail(), passwordChanging.getNewPassword(), false);
                if (updatedRows > 0) {
                    return new ServiceResult<>(true, getTranslation(Translations.Message.YOUR_PASSWORD_HAS_BEEN_CHANGED));
                } else {
                    return new ServiceResult(false, getTranslation(Translations.Message.COMMON_USER_ERROR));
                }
            } else {
                return new ServiceResult<>(false, validationResult.getMessage());
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return new ServiceResult(false, getTranslation(Translations.Message.COMMON_USER_ERROR));
        }
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

    void sendTempPasswordEmail(String email, String tempPassword) throws MessagingException {
        NotificationEmail notificationEmail = new NotificationEmail();
        notificationEmail.setSubject(getTranslation(Translations.Message.NEW_PASSWORD_EMAIL_SUBJECT));
        notificationEmail.setBody(getTranslation(Translations.Message.NEW_PASSWORD_EMAIL_BODY) + " " + tempPassword);
        notificationEmail.setEmailTo(email);
        EmailUtil.sendEmail(notificationEmail);
    }

    private ValidationResult validatePassword(CompanyUserPasswordChanging passwordChanging) {
        if (passwordChanging.getNewPassword() == null || passwordChanging.getPasswordConfirmation() == null) {
            return new ValidationResult(false, getTranslation(Translations.Message.PASSWORD_MUST_HAVE_AT_LEAST_6_CHARACTERS));
        }
        if (passwordChanging.getNewPassword().length() < 6) {
            return new ValidationResult(false, getTranslation(Translations.Message.PASSWORD_MUST_HAVE_AT_LEAST_6_CHARACTERS));
        }
        if (!passwordChanging.getNewPassword().equals(passwordChanging.getPasswordConfirmation())) {
            return new ValidationResult(false, getTranslation(Translations.Message.PASSWORD_AND_CONFIRMATION_ARE_DIFFERENT));
        }
        return new ValidationResult(true, "");
    }
}
