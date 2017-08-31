package com.lealpoints.service.implementations;

import com.lealpoints.context.ThreadContextService;
import com.lealpoints.i18n.Message;
import com.lealpoints.model.CompanyUser;
import com.lealpoints.model.NotificationEmail;
import com.lealpoints.repository.CompanyRepository;
import com.lealpoints.repository.CompanyUserRepository;
import com.lealpoints.service.CompanyService;
import com.lealpoints.service.CompanyUserService;
import com.lealpoints.service.NotificationService;
import com.lealpoints.service.model.*;
import com.lealpoints.service.response.ServiceMessage;
import com.lealpoints.service.response.ServiceResult;
import com.lealpoints.service.util.ServiceUtil;
import com.lealpoints.util.EmailUtil;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;

@Component
public class CompanyUserServiceImpl extends BaseServiceImpl implements CompanyUserService {
    private static final Logger logger = LogManager.getLogger(CompanyUserServiceImpl.class.getName());
    private final CompanyUserRepository companyUserRepository;
    private final CompanyRepository companyRepository;
    private final CompanyService companyService;
    private final NotificationService notificationService;
    private final ServiceUtil _serviceUtil;

    @Autowired
    public CompanyUserServiceImpl(CompanyUserRepository companyUserRepository, ThreadContextService threadContextService,
                                  CompanyRepository companyRepository, CompanyService companyService, NotificationService notificationService, ServiceUtil serviceUtil) {
        super(threadContextService);
        this.companyUserRepository = companyUserRepository;
        this.companyRepository = companyRepository;
        this.companyService = companyService;
        this.notificationService = notificationService;
        _serviceUtil = serviceUtil;
    }

    public ServiceResult<CompanyLoginResult> loginUser(CompanyUserLogin companyUserLogin) {
        CompanyLoginResult loginResult = new CompanyLoginResult();
        try {
            final ValidationResult validateCredentials = validateUserLoginCredentials(companyUserLogin);
            if (validateCredentials.isInvalid()) {
                return new ServiceResult<>(false, validateCredentials.getServiceMessage());
            } else {
                xyz.greatapp.libs.service.ServiceResult companyUser =
                        companyUserRepository.getByEmailAndPassword(companyUserLogin.getEmail(), companyUserLogin.getPassword());
                final ValidationResult validateLogin = validateUserLogin(companyUser, loginResult);
                if (validateLogin.isInvalid()) {
                    return new ServiceResult<>(false, validateLogin.getServiceMessage(), loginResult);
                } else {
                    JSONObject jsonObject = new JSONObject(companyUser.getObject());
                    String apiKey = updateApiKey(jsonObject.getString("email"));
                    loginResult.setEmail(jsonObject.getString("email"));
                    loginResult.setMustChangePassword(jsonObject.getBoolean("must_change_password"));
                    loginResult.setActive(true);
                    loginResult.setCompanyId(jsonObject.getLong("company_id"));
                    loginResult.setCompanyUserId(jsonObject.getLong("company_user_id"));
                    loginResult.setLanguage(jsonObject.getString("language"));
                    xyz.greatapp.libs.service.ServiceResult serviceResult = companyRepository.getByCompanyId(jsonObject.getLong("company_id"));
                    String name = new JSONObject(serviceResult.getObject()).getString("name");
                    loginResult.setCompanyName(name);
                    loginResult.setApiKey(apiKey);
                    return new ServiceResult<>(true, ServiceMessage.EMPTY, loginResult);
                }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return new ServiceResult<>(false, getServiceMessage(Message.COMMON_USER_ERROR));
        }
    }

    public ServiceResult activateUser(String activationKey) {
        try {
            getQueryAgent().beginTransaction();
            int updatedRows = companyUserRepository.updateActivateByActivationKey(activationKey);
            if (updatedRows > 0) {
                companyUserRepository.clearActivationKey(activationKey);
                getQueryAgent().commitTransaction();
                return new ServiceResult(true, getServiceMessage(Message.YOUR_USER_HAS_BEEN_ACTIVATED));
            } else {
                getQueryAgent().rollbackTransaction();
                return new ServiceResult(false, getServiceMessage(Message.COMMON_USER_ERROR));
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return new ServiceResult(false, getServiceMessage(Message.COMMON_USER_ERROR));
        }
    }

    public ServiceResult sendActivationEmail(String email) {
        try {
            final xyz.greatapp.libs.service.ServiceResult companyUser = companyUserRepository.getByEmail(email);
            if ("{}".equals(companyUser.getObject())) {
                return new ServiceResult(false, getServiceMessage(Message.THIS_EMAIL_DOES_NOT_EXIST));
            }
            notificationService.sendActivationEmail(email, new JSONObject(companyUser.getObject()).getString("activation_key"));
            return new ServiceResult(true, getServiceMessage(Message.WE_HAVE_SENT_YOU_AND_ACTIVATION_LINK));
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return new ServiceResult(false, getServiceMessage(Message.COMMON_USER_ERROR));
        }
    }

    public ServiceResult sendTempPasswordEmail(String email) {
        try {
            final xyz.greatapp.libs.service.ServiceResult companyUser = companyUserRepository.getByEmail(email);
            if ("{}".equals(companyUser.getObject())) {
                return new ServiceResult(false, getServiceMessage(Message.THIS_EMAIL_DOES_NOT_EXIST));
            }
            final String tempPassword = RandomStringUtils.random(8, true, true);
            final int updatedRows = companyUserRepository.updatePasswordByEmail(email, tempPassword, true);
            if (updatedRows > 0) {
                sendTempPasswordEmail(email, tempPassword);
            } else {
                return new ServiceResult(false, getServiceMessage(Message.COMMON_USER_ERROR));
            }
            return new ServiceResult(true, getServiceMessage(Message.WE_HAVE_SENT_YOU_A_NEW_PASSWORD_TO_YOUR_EMAIL));
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return new ServiceResult(false, getServiceMessage(Message.COMMON_USER_ERROR));
        }
    }

    public ServiceResult changePassword(CompanyUserPasswordChanging passwordChanging) {
        try {
            final ValidationResult validationResult = validatePassword(passwordChanging);
            if (validationResult.isValid()) {
                final int updatedRows =
                    companyUserRepository.updatePasswordByEmail(passwordChanging.getEmail(), passwordChanging.getNewPassword(), false);
                if (updatedRows > 0) {
                    return new ServiceResult<>(true, getServiceMessage(Message.YOUR_PASSWORD_HAS_BEEN_CHANGED));
                } else {
                    return new ServiceResult(false, getServiceMessage(Message.COMMON_USER_ERROR));
                }
            } else {
                return new ServiceResult<>(false, validationResult.getServiceMessage());
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return new ServiceResult(false, getServiceMessage(Message.COMMON_USER_ERROR));
        }
    }

    void sendTempPasswordEmail(String email, String tempPassword) throws MessagingException {
        NotificationEmail notificationEmail = new NotificationEmail();
        notificationEmail.setSubject(getServiceMessage(Message.NEW_PASSWORD_EMAIL_SUBJECT).getMessage());
        notificationEmail.setBody(getServiceMessage(Message.NEW_PASSWORD_EMAIL_BODY) + " " + tempPassword);
        notificationEmail.setEmailTo(email);
        EmailUtil.sendEmail(notificationEmail);
    }

    private ValidationResult validatePassword(CompanyUserPasswordChanging passwordChanging) {
        if (passwordChanging.getNewPassword() == null || passwordChanging.getPasswordConfirmation() == null) {
            return new ValidationResult(false, getServiceMessage(Message.PASSWORD_MUST_HAVE_AT_LEAST_6_CHARACTERS));
        }
        if (passwordChanging.getNewPassword().length() < 6) {
            return new ValidationResult(false, getServiceMessage(Message.PASSWORD_MUST_HAVE_AT_LEAST_6_CHARACTERS));
        }
        if (!passwordChanging.getNewPassword().equals(passwordChanging.getPasswordConfirmation())) {
            return new ValidationResult(false, getServiceMessage(Message.PASSWORD_AND_CONFIRMATION_ARE_DIFFERENT));
        }
        return new ValidationResult(true, ServiceMessage.EMPTY);
    }

    private ValidationResult validateUserLogin(xyz.greatapp.libs.service.ServiceResult serviceResult, CompanyLoginResult loginResult) throws Exception {
        if ("{}".equals(serviceResult.getObject())) {
            return new ValidationResult(false, getServiceMessage(Message.LOGIN_FAILED));
        }
        if (!new JSONObject(serviceResult.getObject()).getBoolean("active")) {
            loginResult.setActive(false);
            return new ValidationResult(false, getServiceMessage(Message.YOUR_USER_IS_NOT_ACTIVE));
        }
        return new ValidationResult(true, ServiceMessage.EMPTY);
    }

    private ValidationResult validateUserLoginCredentials(CompanyUserLogin companyUserLogin) throws Exception {

        if (StringUtils.isEmpty(companyUserLogin.getEmail())) {
            logger.error("The company user email is empty");
            return new ValidationResult(false, getServiceMessage(Message.EMAIL_IS_EMPTY));
        }
        if (StringUtils.isEmpty(companyUserLogin.getPassword())) {
            logger.error("The company user password is empty");
            return new ValidationResult(false, getServiceMessage(Message.PASSWORD_IS_EMPTY));
        }
        return new ValidationResult(true, ServiceMessage.EMPTY);
    }

    public ServiceResult<String> register(CompanyUserRegistration companyUserRegistration) {
        try {
            ValidationResult validationResult = validateRegistration(companyUserRegistration);
            if (validationResult.isValid()) {
                getThreadContextService().getQueryAgent().beginTransaction();
                long companyUserId = registerCompanyUser(companyUserRegistration);
                getQueryAgent().commitTransaction();
                return new ServiceResult<>(true, getServiceMessage(Message.USER_SUCCESSFULLY_ADDED),
                        Long.toString(companyUserId));
            } else {
                return new ServiceResult<>(false, validationResult.getServiceMessage());
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return new ServiceResult<>(false, getServiceMessage(Message.COMMON_USER_ERROR), null);
        }
    }

    public xyz.greatapp.libs.service.ServiceResult getByCompanyId(long companyId) {
        try {
            return companyUserRepository.getByCompanyId(companyId);
        } catch (Exception ex){
            logger.error(ex.getMessage(), ex);
            return new xyz.greatapp.libs.service.ServiceResult(false, "", null);
        }
    }

    private ValidationResult validateRegistration(CompanyUserRegistration companyUserRegistration) throws Exception {
        //Validate name
        if (StringUtils.isEmpty(companyUserRegistration.getName())) {
            return new ValidationResult(false, getServiceMessage(Message.COMPANY_NAME_IS_EMPTY));
        }
        //Validate user email
        if (StringUtils.isEmpty(companyUserRegistration.getEmail())) {
            return new ValidationResult(false, getServiceMessage(Message.EMAIL_IS_EMPTY));
        }
        if (!EmailValidator.getInstance().isValid(companyUserRegistration.getEmail())) {
            return new ValidationResult(false, getServiceMessage(Message.EMAIL_IS_INVALID));
        }
        if (companyUserRepository.getByEmail(companyUserRegistration.getEmail()) != null) {
            return new ValidationResult(false, getServiceMessage(Message.EMAIL_ALREADY_EXISTS));
        }
        return new ValidationResult(true, ServiceMessage.EMPTY);
    }

    private String updateApiKey(String email) throws Exception {
        String apiKey = RandomStringUtils.random(20, true, true) + "com";
        final int updatedRows = companyUserRepository.updateApiKeyByEmail(email, apiKey);
        if (updatedRows != 1) {
            logger.error("The company user api key could not be updated. updatedRows: " + updatedRows);
            throw new RuntimeException(getServiceMessage(Message.COMMON_USER_ERROR).getMessage());
        }
        return apiKey;
    }

    private Long registerCompanyUser(CompanyUserRegistration companyUserRegistration) throws Exception {
        CompanyUser companyUser = new CompanyUser();
        companyUser.setCompanyId(companyUserRegistration.getCompanyId());
        companyUser.setName(companyUserRegistration.getName());
        companyUser.setPassword(_serviceUtil.generatePassword());
        companyUser.setEmail(companyUserRegistration.getEmail());
        companyService.setUserActivation(companyUser);
        companyUser.setActivationKey(_serviceUtil.generateActivationKey());
        String language = getThreadContext().getLanguage().getLangId();
        companyUser.setLanguage(language);
        companyUser.setMustChangePassword(true);
        companyUser.setCompanyUserId(companyUserRepository.insert(companyUser));
        notificationService.sendActivationEmail(companyUser,
                getServiceMessage(Message.ACTIVATION_EMAIL_SUBJECT).getMessage(),
                getServiceMessage(Message.TEMPORAL_PASSWORD).getMessage() + "  " + companyUser.getPassword(),
                getServiceMessage(Message.ACTIVATION_EMAIL_BODY).getMessage());
        return companyUser.getCompanyUserId();
    }
}
