package com.neerpoints.service;

import javax.mail.MessagingException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import com.neerpoints.context.ThreadContextService;
import com.neerpoints.model.Client;
import com.neerpoints.model.ClientUser;
import com.neerpoints.model.NotificationEmail;
import com.neerpoints.repository.ClientRepository;
import com.neerpoints.repository.ClientUserRepository;
import com.neerpoints.service.model.ClientUserLogin;
import com.neerpoints.service.model.ClientUserRegistration;
import com.neerpoints.service.model.ServiceResult;
import com.neerpoints.service.model.ValidationResult;
import com.neerpoints.util.EmailUtil;
import com.neerpoints.util.Translations;
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

    @Autowired
    public ClientUserService(ClientUserRepository clientUserRepository, ClientRepository clientRepository, ThreadContextService threadContextService,
        Translations translations) {
        super(translations, threadContextService);
        _clientUserRepository = clientUserRepository;
        _clientRepository = clientRepository;
        _threadContextService = threadContextService;
    }

    public ServiceResult<Long> register(ClientUserRegistration clientUserRegistration) {
        try {
            ValidationResult validationResult = validateRegistration(clientUserRegistration);
            if (validationResult.isSuccess()) {
                _threadContextService.getQueryAgent().beginTransaction();
                long clientUserId = registerClientAndClientUser(clientUserRegistration);
                _threadContextService.getQueryAgent().commitTransaction();
                return new ServiceResult<>(true, "", clientUserId);
            } else {
                return new ServiceResult<>(false, validationResult.getMessage());
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return new ServiceResult<>(false, getTranslation(Translations.Message.COMMON_USER_ERROR), null);
        }
    }

    public ServiceResult<Long> login(ClientUserLogin clientUserLogin) {
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
                return new ServiceResult<>(true, "", clientUser.getClientUserId());
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return new ServiceResult<>(false, getTranslation(Translations.Message.COMMON_USER_ERROR));
        }
    }

    public ServiceResult<Boolean> resendKey(String phone) {
        try {
            _clientUserRepository.updateSmsKey(generateAndSendSms(phone), phone);
            return new ServiceResult<>(true, "", true);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return new ServiceResult<>(false, getTranslation(Translations.Message.COMMON_USER_ERROR));
        }
    }

    private long registerClientAndClientUser(ClientUserRegistration clientUserRegistration) throws Exception {
        Client client = _clientRepository.insertIfDoesNotExist(clientUserRegistration.getPhone());
        ClientUser clientUser = _clientUserRepository.getByClientId(client.getClientId());
        if (clientUser == null) {
            clientUser = new ClientUser();
            clientUser.setClientId(client.getClientId());
            clientUser.setSmsKey(generateAndSendSms(clientUserRegistration.getPhone()));
            return _clientUserRepository.insert(clientUser);
        } else {
            _clientUserRepository.updateSmsKey(generateAndSendSms(clientUserRegistration.getPhone()), clientUserRegistration.getPhone());
            return clientUser.getClientUserId();
        }
    }

    private ClientUser authenticateUsingPhone(ClientUserLogin clientUserLogin) throws Exception {
        return _clientUserRepository.getByPhoneAndKey(clientUserLogin.getPhone(), clientUserLogin.getSmsKey());
    }

    private ClientUser authenticateUsingEmail(ClientUserLogin clientUserLogin) throws Exception {
        return _clientUserRepository.getByEmailAndPassword(clientUserLogin.getEmail(), clientUserLogin.getPassword());
    }

    String generateAndSendSms(String phone) throws MessagingException, IOException {
        String key = RandomStringUtils.random(6, false, true);
        if (isProdEnvironment()) {
            HttpURLConnection connection = null;
            InputStreamReader in = null;
            StringBuilder sb = new StringBuilder();
            try {
                String keyMessage = getTranslation(Translations.Message.KEY_EMAIL_SMS_MESSAGE) + " " + key;
                keyMessage = keyMessage.replaceAll(" ", "%20");
                URL url = new URL(
                    "https://www.masmensajes.com.mx/wss/smsapi11.php?usuario=alayor&password=d48a47&celular=+52" + phone + "&mensaje=" + keyMessage);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                if (connection.getInputStream() != null) {
                    in = new InputStreamReader(connection.getInputStream(), Charset.defaultCharset());
                    BufferedReader bufferedReader = new BufferedReader(in);
                    int cp;
                    while ((cp = bufferedReader.read()) != -1) {
                        sb.append((char) cp);
                    }
                    bufferedReader.close();
                }
                if (!sb.toString().contains("OK")) {
                    throw new IllegalArgumentException("SMS Message could not be sent. phone: " + phone + ". key: " + key);
                }
                assert in != null;
                in.close();
                sendKeyToEmail(key, phone);
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
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
