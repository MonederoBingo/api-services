package com.neerpoints.util;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import com.neerpoints.model.NotificationEmail;

public class EmailUtil {

    public static void sendEmail(NotificationEmail notificationEmail) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("neerpoints@gmail.com", "aayala88");
            }
        });
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("neerpoints@gmail.com"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(notificationEmail.getEmailTo()));
        message.setSubject(notificationEmail.getSubject());
        message.setContent(notificationEmail.getBody(), "text/html; charset=utf-8");
        Transport.send(message);
    }
}
