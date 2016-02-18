package com.lealpoints.service.implementations;

import com.lealpoints.common.PropertyManager;
import com.lealpoints.context.ThreadContextService;
import com.lealpoints.service.ConfigurationService;
import com.lealpoints.service.SMSService;
import com.lealpoints.service.annotation.OnlyProduction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

@Component
public class SMSServiceImpl extends BaseServiceImpl implements SMSService {
    private final ConfigurationService configurationService;

    @Autowired
    public SMSServiceImpl(ThreadContextService threadContextService, ConfigurationService configurationService) {
        super(threadContextService);
        this.configurationService = configurationService;
    }

    @OnlyProduction
    public void sendSMSMessage(String phone, String message) throws IOException, MessagingException {
        final boolean sendPromoSmsInUat =
                Boolean.parseBoolean(configurationService.getUncachedConfiguration("send_promo_sms_in_uat"));
        if (isProdEnvironment() || sendPromoSmsInUat) {
            HttpURLConnection connection = null;
            try {
                message = message.replaceAll(" ", "%20");
                URL url = getUrl(phone, message);
                connection = getHttpURLConnection(url);
                connection.setRequestMethod("GET");
                String response = getResponse(connection);
                if (!response.contains("OK")) {
                    throw new IllegalArgumentException("SMS Message could not be sent. phone: " + phone + ". message: " + message);
                }
            } catch (Exception e) {
                throw new RuntimeException("SMS Message could not be sent. phone: " + phone + ". message: " + message);
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }
    }

    public String getResponse(HttpURLConnection connection) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader in = null;
        if (connection.getInputStream() != null) {
            in = getInputStreamReader(connection);
            BufferedReader bufferedReader = new BufferedReader(in);
            int cp;
            while ((cp = bufferedReader.read()) != -1) {
                sb.append((char) cp);
            }
            bufferedReader.close();
        }
        assert in != null;
        in.close();
        return sb.toString();
    }

    URL getUrl(String phone, String message) throws MalformedURLException {
        final String providerSmsApi = PropertyManager.getProperties().getProperty("provider_sms_api");
        return new URL(providerSmsApi + phone + "&mensaje=" + message);
    }

    InputStreamReader getInputStreamReader(HttpURLConnection connection) throws IOException {
        return new InputStreamReader(connection.getInputStream(), Charset.defaultCharset());
    }

    HttpURLConnection getHttpURLConnection(URL url) throws IOException {
        return (HttpURLConnection) url.openConnection();
    }
}
