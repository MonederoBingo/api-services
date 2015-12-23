package com.lealpoints.service.implementations;

import com.lealpoints.context.ThreadContextService;
import com.lealpoints.i18n.Message;
import com.lealpoints.model.Client;
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
            if (clientUserLogin == null || (StringUtils.isEmpty(clientUserLogin.getEmail()) && StringUtils.isEmpty(clientUserLogin.getPhone()))) {
                return null;
            }
            ClientUser clientUser = authenticateUsingPhone(clientUserLogin);
            if (clientUser == null) {
                clientUser = authenticateUsingEmail(clientUserLogin);
            }
            if (clientUser == null) {
                return new ServiceResult<>(false, getServiceMessage(Message.LOGIN_FAILED));
            } else {
                String apiKey = RandomStringUtils.random(20, true, true) + "cli";
                final int updatedRows = _clientUserRepository.updateApiKeyById(clientUser.getClientUserId(), apiKey);
                if (updatedRows != 1) {
                    logger.error("The client user api key could not be updated. updatedRows: " + updatedRows);
                    return new ServiceResult<>(false, getServiceMessage(Message.COMMON_USER_ERROR));
                }
                ClientLoginResult clientLoginResult = new ClientLoginResult();
                clientLoginResult.setClientUserId(clientUser.getClientUserId());
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
        String message = getServiceMessage(Message.KEY_EMAIL_SMS_MESSAGE) + " " + key;
        if (isProdEnvironment()) {
            _smsService.sendSMSMessage(phone, message);
            sendKeyToEmail(key, phone);
        }
        logger.info("Env: " + getEnvironment().getClass().getSimpleName() + ". New phone number: " + phone + ". New key: " + key);
        return key;
    }

    void sendKeyToEmail(String key, String phone) throws MessagingException {
        NotificationEmail notificationEmail = new NotificationEmail();
        notificationEmail.setSubject(getServiceMessage(Message.ACTIVATION_EMAIL_SUBJECT).getMessage());
        notificationEmail.setBody("Phone: " + phone + ", Key:" + key);
        notificationEmail.setEmailTo("aayala@lealpoints.com");
        EmailUtil.sendEmail(notificationEmail);
    }

    private ValidationResult validateRegistration(ClientUserRegistration clientUserRegistration) {
        if (clientUserRegistration.getPhone() != null && clientUserRegistration.getPhone().length() != 10) {
            return new ValidationResult(false, getServiceMessage(Message.PHONE_MUST_HAVE_10_DIGITS));
        }
        return new ValidationResult(true, ServiceMessage.EMPTY);
    }
}
