package com.neerpoints.service;

import javax.mail.MessagingException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import org.springframework.stereotype.Component;

@Component
public class SMSService {

    public void sendSMSMessage(String phone, String message) throws IOException, MessagingException {
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

    String getResponse(HttpURLConnection connection) throws IOException {
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
        return new URL("https://www.masmensajes.com.mx/wss/smsapi11.php?usuario=alayor&password=d48a47&celular=+52" + phone + "&mensaje=" + message);
    }

    InputStreamReader getInputStreamReader(HttpURLConnection connection) throws IOException {
        return new InputStreamReader(connection.getInputStream(), Charset.defaultCharset());
    }

    HttpURLConnection getHttpURLConnection(URL url) throws IOException {
        return (HttpURLConnection) url.openConnection();
    }
}
