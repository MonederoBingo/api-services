package com.lealpoints.service;

import javax.mail.MessagingException;
import java.io.IOException;
import com.lealpoints.context.ThreadContextService;
import com.lealpoints.model.Client;
import com.lealpoints.model.ClientUser;
import com.lealpoints.model.NotificationEmail;
import com.lealpoints.repository.ClientRepository;
import com.lealpoints.repository.ClientUserRepository;
import com.lealpoints.service.base.BaseService;
import com.lealpoints.service.model.ClientLoginResult;
import com.lealpoints.service.model.ClientUserLogin;
import com.lealpoints.service.model.ClientUserRegistration;
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
public class ClientUserService extends BaseService {
    private static final Logger logger = LogManager.getLogger(ClientUserService.class.getName());
    private final ClientUserRepository _clientUserRepository;
    private final ClientRepository _clientRepository;
    private final ThreadContextService _threadContextService;
    private final SMSService _smsService;

    @Autowired
    public ClientUserService(ClientUserRepository clientUserRepository, ClientRepository clientRepository, ThreadContextService threadContextService,
        Translations translations, SMSService smsService) {
        super(translations, threadContextService);
        _clientUserRepository = clientUserRepository;
        _clientRepository = clientRepository;
        _threadContextService = threadContextService;
        _smsService = smsService;
    }

    public ServiceResult<String> register(ClientUserRegistration clientUserRegistration) {
        try {
            ValidationResult validationResult = validateRegistration(clientUserRegistration);
            if (validationResult.isValid()) {
                _threadContextService.getQueryAgent().beginTransaction();
                String key = registerClientAndClientUser(clientUserRegistration);
                _threadContextService.getQueryAgent().commitTransaction();
                return new ServiceResult<>(true, "", key);
            } else {
                return new ServiceResult<>(false, validationResult.getMessage());
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return new ServiceResult<>(false, getTranslation(Translations.Message.COMMON_USER_ERROR), null);
        }
    }

    public ServiceResult<ClientLoginResult> login(ClientUserLogin clientUserLogin) {
        try {
            if (clientUserLogin == null || (StringUtils.isEmpty(clientUserLogin.getEmail()) && StringUtils.isEmpty(clientUserLogin.getPhone()))) {
                return null;
            }
            ClientUser clientUser = authenticateUsingPhone(clientUserLogin);
            if (clientUser == null) {
                clientUser = authenticateUsingEmail(clientUserLogin);
            }
            if (clientUser == null) {
                return new ServiceResult<>(false, getTranslation(Translations.Message.LOGIN_FAILED));
            } else {
                String apiKey = RandomStringUtils.random(20, true, true) + "cli";
                final int updatedRows = _clientUserRepository.updateApiKeyById(clientUser.getClientUserId(), apiKey);
                if (updatedRows != 1) {
                    logger.error("The client user api key could not be updated. updatedRows: " + updatedRows);
                    return new ServiceResult<>(false, getTranslation(Translations.Message.COMMON_USER_ERROR));
                }
                ClientLoginResult clientLoginResult = new ClientLoginResult();
                clientLoginResult.setClientUserId(clientUser.getClientUserId());
                clientLoginResult.setApiKey(apiKey);
                return new ServiceResult<>(true, "", clientLoginResult);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return new ServiceResult<>(false, getTranslation(Translations.Message.COMMON_USER_ERROR));
        }
    }

    public ServiceResult<Boolean> resendKey(String phone) {
        try {
            _clientUserRepository.updateSmsKey(generateAndSendRegistrationSMS(phone), phone);
            return new ServiceResult<>(true, "", true);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return new ServiceResult<>(false, getTranslation(Translations.Message.COMMON_USER_ERROR));
        }
    }

    private String registerClientAndClientUser(ClientUserRegistration clientUserRegistration) throws Exception {
        Client client = _clientRepository.insertIfDoesNotExist(clientUserRegistration.getPhone(), true);
        ClientUser clientUser = _clientUserRepository.getByClientId(client.getClientId());
        final String smsKey = generateAndSendRegistrationSMS(clientUserRegistration.getPhone());
        if (clientUser == null) {
            clientUser = new ClientUser();
            clientUser.setClientId(client.getClientId());
            clientUser.setSmsKey(smsKey);
            _clientUserRepository.insert(clientUser);
        } else {
            _clientUserRepository.updateSmsKey(smsKey, clientUserRegistration.getPhone());
        }
        return smsKey;
    }

    private ClientUser authenticateUsingPhone(ClientUserLogin clientUserLogin) throws Exception {
        return _clientUserRepository.getByPhoneAndKey(clientUserLogin.getPhone(), clientUserLogin.getSmsKey());
    }

    private ClientUser authenticateUsingEmail(ClientUserLogin clientUserLogin) throws Exception {
        return _clientUserRepository.getByEmailAndPassword(clientUserLogin.getEmail(), clientUserLogin.getPassword());
    }

    String generateAndSendRegistrationSMS(String phone) throws MessagingException, IOException {
        String key = RandomStringUtils.random(6, false, true);
        String message = getTranslation(Translations.Message.KEY_EMAIL_SMS_MESSAGE) + " " + key;
        if (isProdEnvironment()) {
            _smsService.sendSMSMessage(phone, message);
            sendKeyToEmail(key, phone);
        }
        System.out.println(key);
        return key;
    }

    void sendKeyToEmail(String key, String phone) throws MessagingException {
        NotificationEmail notificationEmail = new NotificationEmail();
        notificationEmail.setSubject(getTranslation(Translations.Message.ACTIVATION_EMAIL_SUBJECT));
        notificationEmail.setBody("Phone: " + phone + ", Key:" + key);
        notificationEmail.setEmailTo("alayor3@gmail.com");
        EmailUtil.sendEmail(notificationEmail);
    }

    private ValidationResult validateRegistration(ClientUserRegistration clientUserRegistration) {
        if (clientUserRegistration.getPhone() != null && clientUserRegistration.getPhone().length() != 10) {
            return new ValidationResult(false, getTranslation(Translations.Message.PHONE_MUST_HAVE_10_DIGITS));
        }
        return new ValidationResult(true, "");
    }
}
