package com.lealpoints.service.implementations;

import javax.mail.MessagingException;
import com.lealpoints.context.ThreadContextService;
import com.lealpoints.model.CompanyUser;
import com.lealpoints.model.NotificationEmail;
import com.lealpoints.repository.CompanyRepository;
import com.lealpoints.repository.CompanyUserRepository;
import com.lealpoints.service.model.CompanyLoginResult;
import com.lealpoints.service.model.CompanyUserLogin;
import com.lealpoints.service.model.CompanyUserPasswordChanging;
import com.lealpoints.service.model.ServiceResult;
import com.lealpoints.service.model.ValidationResult;
import com.lealpoints.util.EmailUtil;
import com.lealpoints.util.Translations;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CompanyUserServiceImpl extends BaseServiceImpl {
    private static final Logger logger = LogManager.getLogger(CompanyUserServiceImpl.class.getName());
    private final CompanyUserRepository _companyUserRepository;
    private final CompanyRepository _companyRepository;
    private final ThreadContextService _threadContextService;
    private final CompanyServiceImpl _companyService;

    @Autowired
    public CompanyUserServiceImpl(CompanyUserRepository companyUserRepository, ThreadContextService threadContextService, Translations translations,
        CompanyRepository companyRepository, CompanyServiceImpl companyService) {
        super(translations, threadContextService);
        _companyUserRepository = companyUserRepository;
        _threadContextService = threadContextService;
        _companyRepository = companyRepository;
        _companyService = companyService;
    }

    public ServiceResult<CompanyLoginResult> loginUser(CompanyUserLogin companyUserLogin) {
        CompanyLoginResult loginResult = new CompanyLoginResult();
        try {
            final ValidationResult validateCredentials = validateUserLoginCredentials(companyUserLogin);
            if (validateCredentials.isValid()) {
                CompanyUser companyUser = _companyUserRepository.getByEmailAndPassword(companyUserLogin.getEmail(), companyUserLogin.getPassword());
                final ValidationResult validateLogin = validateUserLogin(companyUser, loginResult);
                if (validateLogin.isValid()) {
                    String apiKey = updateApiKey(companyUser);
                    loginResult.setEmail(companyUser.getEmail());
                    loginResult.setMustChangePassword(companyUser.getMustChangePassword());
                    loginResult.setActive(true);
                    loginResult.setCompanyId(companyUser.getCompanyId());
                    loginResult.setCompanyUserId(companyUser.getCompanyUserId());
                    loginResult.setLanguage(companyUser.getLanguage());
                    loginResult.setCompanyName(_companyRepository.getByCompanyId(companyUser.getCompanyId()).getName());
                    loginResult.setApiKey(apiKey);
                    return new ServiceResult<>(true, "", loginResult);
                } else {
                    return new ServiceResult<>(false, validateLogin.getMessage(), loginResult);
                }
            } else {
                return new ServiceResult<>(false, validateCredentials.getMessage());
            }
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
            _companyService.sendActivationEmail(email, companyUser.getActivationKey());
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
            if (validationResult.isValid()) {
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

    private ValidationResult validateUserLogin(CompanyUser companyUser, CompanyLoginResult loginResult) throws Exception {
        if (companyUser == null) {
            return new ValidationResult(false, getTranslation(Translations.Message.LOGIN_FAILED));
        }
        if (!companyUser.isActive()) {
            loginResult.setActive(false);
            return new ValidationResult(false, getTranslation(Translations.Message.YOUR_USER_IS_NOT_ACTIVE));
        }
        return new ValidationResult(true, "");
    }

    private ValidationResult validateUserLoginCredentials(CompanyUserLogin companyUserLogin) throws Exception {

        if (StringUtils.isEmpty(companyUserLogin.getEmail())) {
            logger.error("The company user email is empty");
            return new ValidationResult(false, getTranslation(Translations.Message.EMAIL_IS_EMPTY));
        }
        if (StringUtils.isEmpty(companyUserLogin.getPassword())) {
            logger.error("The company user password is empty");
            return new ValidationResult(false, getTranslation(Translations.Message.PASSWORD_IS_EMPTY));
        }
        return new ValidationResult(true, "");
    }

    private String updateApiKey(CompanyUser companyUser) throws Exception {
        String apiKey = RandomStringUtils.random(20, true, true) + "com";
        final int updatedRows = _companyUserRepository.updateApiKeyByEmail(companyUser.getEmail(), apiKey);
        if (updatedRows != 1) {
            logger.error("The company user api key could not be updated. updatedRows: " + updatedRows);
            throw new RuntimeException(getTranslation(Translations.Message.COMMON_USER_ERROR));
        }
        return apiKey;
    }
}
