package com.lealpoints.service;

import javax.mail.MessagingException;
import java.io.IOException;
import java.net.HttpURLConnection;

public interface SMSService extends BaseService {

    public void sendSMSMessage(String phone, String message) throws IOException, MessagingException;

    String getResponse(HttpURLConnection connection) throws IOException;
}
