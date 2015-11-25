package com.lealpoints.service.implementations;

import com.lealpoints.context.ThreadContextService;
import com.lealpoints.i18n.Message;
import com.lealpoints.model.CompanyUser;
import com.lealpoints.model.NotificationEmail;
import com.lealpoints.repository.CompanyRepository;
import com.lealpoints.repository.CompanyUserRepository;
import com.lealpoints.service.CompanyUserService;
import com.lealpoints.service.model.*;
import com.lealpoints.util.EmailUtil;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;

@Component
public class CompanyUserServiceImpl extends BaseServiceImpl implements CompanyUserService {
    private static final Logger logger = LogManager.getLogger(CompanyUserServiceImpl.class.getName());
    private final CompanyUserRepository _companyUserRepository;
    private final CompanyRepository _companyRepository;
    private final CompanyServiceImpl _companyService;

    @Autowired
    public CompanyUserServiceImpl(CompanyUserRepository companyUserRepository, ThreadContextService threadContextService,
                                  CompanyRepository companyRepository, CompanyServiceImpl companyService) {
        super(threadContextService);
        _companyUserRepository = companyUserRepository;
        _companyRepository = companyRepository;
        _companyService = companyService;
    }

    public ServiceResult<CompanyLoginResult> loginUser(CompanyUserLogin companyUserLogin) {
        CompanyLoginResult loginResult = new CompanyLoginResult();
        try {
            final ValidationResult validateCredentials = validateUserLoginCredentials(companyUserLogin);
            if (validateCredentials.isInvalid()) {
                return new ServiceResult<>(false, validateCredentials.getMessage());
            } else {
                CompanyUser companyUser = _companyUserRepository.getByEmailAndPassword(companyUserLogin.getEmail(), companyUserLogin.getPassword());
                final ValidationResult validateLogin = validateUserLogin(companyUser, loginResult);
                if (validateLogin.isInvalid()) {
                    return new ServiceResult<>(false, validateLogin.getMessage(), loginResult);
                } else {
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
                }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return new ServiceResult<>(false, getTranslation(Message.COMMON_USER_ERROR));
        }
    }

    public ServiceResult activateUser(String activationKey) {
        try {
            getQueryAgent().beginTransaction();
            int updatedRows = _companyUserRepository.updateActivateByActivationKey(activationKey);
            if (updatedRows > 0) {
                _companyUserRepository.clearActivationKey(activationKey);
                getQueryAgent().commitTransaction();
                return new ServiceResult(true, getTranslation(Message.YOUR_USER_HAS_BEEN_ACTIVATED));
            } else {
                getQueryAgent().rollbackTransaction();
                return new ServiceResult(false, getTranslation(Message.COMMON_USER_ERROR));
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return new ServiceResult(false, getTranslation(Message.COMMON_USER_ERROR));
        }
    }

    public ServiceResult sendActivationEmail(String email) {
        try {
            final CompanyUser companyUser = _companyUserRepository.getByEmail(email);
            if (companyUser == null) {
                return new ServiceResult(false, getTranslation(Message.THIS_EMAIL_DOES_NOT_EXIST));
            }
            _companyService.sendActivationEmail(email, companyUser.getActivationKey());
            return new ServiceResult(true, getTranslation(Message.WE_HAVE_SENT_YOU_AND_ACTIVATION_LINK));
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return new ServiceResult(false, getTranslation(Message.COMMON_USER_ERROR));
        }
    }

    public ServiceResult sendTempPasswordEmail(String email) {
        try {
            final CompanyUser companyUser = _companyUserRepository.getByEmail(email);
            if (companyUser == null) {
                return new ServiceResult(false, getTranslation(Message.THIS_EMAIL_DOES_NOT_EXIST));
            }
            final String tempPassword = RandomStringUtils.random(8, true, true);
            final int updatedRows = _companyUserRepository.updatePasswordByEmail(email, tempPassword, true);
            if (updatedRows > 0) {
                sendTempPasswordEmail(email, tempPassword);
            } else {
                return new ServiceResult(false, getTranslation(Message.COMMON_USER_ERROR));
            }
            return new ServiceResult(true, getTranslation(Message.WE_HAVE_SENT_YOU_A_NEW_PASSWORD_TO_YOUR_EMAIL));
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return new ServiceResult(false, getTranslation(Message.COMMON_USER_ERROR));
        }
    }

    public ServiceResult changePassword(CompanyUserPasswordChanging passwordChanging) {
        try {
            final ValidationResult validationResult = validatePassword(passwordChanging);
            if (validationResult.isValid()) {
                final int updatedRows =
                    _companyUserRepository.updatePasswordByEmail(passwordChanging.getEmail(), passwordChanging.getNewPassword(), false);
                if (updatedRows > 0) {
                    return new ServiceResult<>(true, getTranslation(Message.YOUR_PASSWORD_HAS_BEEN_CHANGED));
                } else {
                    return new ServiceResult(false, getTranslation(Message.COMMON_USER_ERROR));
                }
            } else {
                return new ServiceResult<>(false, validationResult.getMessage());
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return new ServiceResult(false, getTranslation(Message.COMMON_USER_ERROR));
        }
    }

    void sendTempPasswordEmail(String email, String tempPassword) throws MessagingException {
        NotificationEmail notificationEmail = new NotificationEmail();
        notificationEmail.setSubject(getTranslation(Message.NEW_PASSWORD_EMAIL_SUBJECT));
        notificationEmail.setBody(getTranslation(Message.NEW_PASSWORD_EMAIL_BODY) + " " + tempPassword);
        notificationEmail.setEmailTo(email);
        EmailUtil.sendEmail(notificationEmail);
    }

    private ValidationResult validatePassword(CompanyUserPasswordChanging passwordChanging) {
        if (passwordChanging.getNewPassword() == null || passwordChanging.getPasswordConfirmation() == null) {
            return new ValidationResult(false, getTranslation(Message.PASSWORD_MUST_HAVE_AT_LEAST_6_CHARACTERS));
        }
        if (passwordChanging.getNewPassword().length() < 6) {
            return new ValidationResult(false, getTranslation(Message.PASSWORD_MUST_HAVE_AT_LEAST_6_CHARACTERS));
        }
        if (!passwordChanging.getNewPassword().equals(passwordChanging.getPasswordConfirmation())) {
            return new ValidationResult(false, getTranslation(Message.PASSWORD_AND_CONFIRMATION_ARE_DIFFERENT));
        }
        return new ValidationResult(true, "");
    }

    private ValidationResult validateUserLogin(CompanyUser companyUser, CompanyLoginResult loginResult) throws Exception {
        if (companyUser == null) {
            return new ValidationResult(false, getTranslation(Message.LOGIN_FAILED));
        }
        if (!companyUser.isActive()) {
            loginResult.setActive(false);
            return new ValidationResult(false, getTranslation(Message.YOUR_USER_IS_NOT_ACTIVE));
        }
        return new ValidationResult(true, "");
    }

    private ValidationResult validateUserLoginCredentials(CompanyUserLogin companyUserLogin) throws Exception {

        if (StringUtils.isEmpty(companyUserLogin.getEmail())) {
            logger.error("The company user email is empty");
            return new ValidationResult(false, getTranslation(Message.EMAIL_IS_EMPTY));
        }
        if (StringUtils.isEmpty(companyUserLogin.getPassword())) {
            logger.error("The company user password is empty");
            return new ValidationResult(false, getTranslation(Message.PASSWORD_IS_EMPTY));
        }
        return new ValidationResult(true, "");
    }

    private String updateApiKey(CompanyUser companyUser) throws Exception {
        String apiKey = RandomStringUtils.random(20, true, true) + "com";
        final int updatedRows = _companyUserRepository.updateApiKeyByEmail(companyUser.getEmail(), apiKey);
        if (updatedRows != 1) {
            logger.error("The company user api key could not be updated. updatedRows: " + updatedRows);
            throw new RuntimeException(getTranslation(Message.COMMON_USER_ERROR));
        }
        return apiKey;
    }
}
