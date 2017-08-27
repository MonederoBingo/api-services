package com.lealpoints.service.implementations;

import com.lealpoints.context.ThreadContextService;
import com.lealpoints.i18n.Message;
import com.lealpoints.model.ClientUser;
import com.lealpoints.model.NotificationEmail;
import com.lealpoints.repository.ClientRepository;
import com.lealpoints.repository.ClientUserRepository;
import com.lealpoints.service.ClientUserService;
import com.lealpoints.service.model.ClientLoginResult;
import com.lealpoints.service.model.ClientUserLogin;
import com.lealpoints.service.model.ClientUserRegistration;
import com.lealpoints.service.model.ValidationResult;
import com.lealpoints.service.response.ServiceMessage;
import com.lealpoints.service.response.ServiceResult;
import com.lealpoints.util.EmailUtil;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.io.IOException;

@Component
public class ClientUserServiceImpl extends BaseServiceImpl implements ClientUserService {
    private static final Logger logger = LogManager.getLogger(ClientUserServiceImpl.class.getName());
    private final ClientUserRepository _clientUserRepository;
    private final ClientRepository _clientRepository;
    private final SMSServiceImpl _smsService;

    @Autowired
    public ClientUserServiceImpl(ClientUserRepository clientUserRepository, ClientRepository clientRepository,
                                 ThreadContextService threadContextService, SMSServiceImpl smsService) {
        super(threadContextService);
        _clientUserRepository = clientUserRepository;
        _clientRepository = clientRepository;
        _smsService = smsService;
    }

    public ServiceResult<String> register(ClientUserRegistration clientUserRegistration) {
        try {
            ValidationResult validationResult = validateRegistration(clientUserRegistration);
            if (validationResult.isValid()) {
                getThreadContextService().getQueryAgent().beginTransaction();
                String key = registerClientAndClientUser(clientUserRegistration);
                getThreadContextService().getQueryAgent().commitTransaction();
                return new ServiceResult<>(true, ServiceMessage.EMPTY, key);
            } else {
                return new ServiceResult<>(false, validationResult.getServiceMessage());
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return new ServiceResult<>(false, getServiceMessage(Message.COMMON_USER_ERROR), null);
        }
    }

    public ServiceResult<ClientLoginResult> login(ClientUserLogin clientUserLogin) {
        try {
            if (clientUserLogin == null || (StringUtils.isEmpty(clientUserLogin.getEmail()) && StringUtils.isEmpty(clientUserLogin.getPhoneNumber()))) {
                return null;
            }
            xyz.greatapp.libs.service.ServiceResult clientUser = authenticateUsingPhone(clientUserLogin);
            if (clientUser.getObject().equals("{}")) {
                clientUser = authenticateUsingEmail(clientUserLogin);
            }
            if (clientUser.getObject().equals("{}")) {
                return new ServiceResult<>(false, getServiceMessage(Message.LOGIN_FAILED));
            } else {
                String apiKey = RandomStringUtils.random(20, true, true) + "cli";
                long clientUserId = new JSONObject(clientUser.getObject()).getLong("client_user_id");
                final int updatedRows = _clientUserRepository.updateApiKeyById(
                        clientUserId, apiKey);
                if (updatedRows != 1) {
                    logger.error("The client user api key could not be updated. updatedRows: " + updatedRows);
                    return new ServiceResult<>(false, getServiceMessage(Message.COMMON_USER_ERROR));
                }
                ClientLoginResult clientLoginResult = new ClientLoginResult();
                clientLoginResult.setClientUserId(clientUserId);
                clientLoginResult.setApiKey(apiKey);
                return new ServiceResult<>(true, ServiceMessage.EMPTY, clientLoginResult);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return new ServiceResult<>(false, getServiceMessage(Message.COMMON_USER_ERROR));
        }
    }

    public ServiceResult<Boolean> resendKey(String phone) {
        try {
            _clientUserRepository.updateSmsKey(generateAndSendRegistrationSMS(phone), phone);
            return new ServiceResult<>(true, ServiceMessage.EMPTY, true);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return new ServiceResult<>(false, getServiceMessage(Message.COMMON_USER_ERROR));
        }
    }

    private String registerClientAndClientUser(ClientUserRegistration clientUserRegistration) throws Exception {
        xyz.greatapp.libs.service.ServiceResult client = _clientRepository.insertIfDoesNotExist(clientUserRegistration.getPhoneNumber(), true);
        long clientId = new JSONObject(client.getObject()).getLong("client_id");
        xyz.greatapp.libs.service.ServiceResult serviceResult = _clientUserRepository.getByClientId(clientId);
        final String smsKey = generateAndSendRegistrationSMS(clientUserRegistration.getPhoneNumber());
        if ("{}".equals(serviceResult.getObject())) {
            ClientUser clientUser = new ClientUser();
            clientUser.setClientId(clientId);
            clientUser.setSmsKey(smsKey);
            _clientUserRepository.insert(clientUser);
        } else {
            _clientUserRepository.updateSmsKey(smsKey, clientUserRegistration.getPhoneNumber());
        }
        return smsKey;
    }

    private xyz.greatapp.libs.service.ServiceResult authenticateUsingPhone(ClientUserLogin clientUserLogin) throws Exception {
        return _clientUserRepository.getByPhoneAndKey(clientUserLogin.getPhoneNumber(), clientUserLogin.getSmsKey());
    }

    private xyz.greatapp.libs.service.ServiceResult authenticateUsingEmail(ClientUserLogin clientUserLogin) throws Exception {
        return _clientUserRepository.getByEmailAndPassword(clientUserLogin.getEmail(), clientUserLogin.getPassword());
    }

    String generateAndSendRegistrationSMS(String phone) throws MessagingException, IOException {
        String key = RandomStringUtils.random(6, false, true);
        String message = getServiceMessage(Message.KEY_EMAIL_SMS_MESSAGE) + " " + key;
        if (isProdEnvironment()) {
            _smsService.sendSMSMessage(phone, message);
            sendKeyToEmail(key, phone);
        }
        if (isDevEnvironment()) {
            logger.info("Env: " + getEnvironment().getClass().getSimpleName() + ". New phone number: " + phone + ". New key: " + key);
            System.out.println(key);
        }
        return key;
    }

    private void sendKeyToEmail(String key, String phone) throws MessagingException {
        NotificationEmail notificationEmail = new NotificationEmail();
        notificationEmail.setSubject(getServiceMessage(Message.ACTIVATION_EMAIL_SUBJECT).getMessage());
        notificationEmail.setBody("Phone: " + phone + ", Key:" + key);
        notificationEmail.setEmailTo("alonso.ayala@monederobingo.com");
        EmailUtil.sendEmail(notificationEmail);
    }

    private ValidationResult validateRegistration(ClientUserRegistration clientUserRegistration) {
        if (clientUserRegistration.getPhoneNumber() != null && clientUserRegistration.getPhoneNumber().length() != 10) {
            return new ValidationResult(false, getServiceMessage(Message.PHONE_MUST_HAVE_10_DIGITS));
        }
        return new ValidationResult(true, ServiceMessage.EMPTY);
    }
}
