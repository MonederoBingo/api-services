package com.lealpoints.service.implementations;

import com.lealpoints.context.ThreadContextService;
import com.lealpoints.model.CompanyUser;
import com.lealpoints.model.NotificationEmail;
import com.lealpoints.util.EmailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;

@Component
public class NotificationService extends BaseServiceImpl{

    @Autowired
    public NotificationService(ThreadContextService threadContextService) {
        super(threadContextService);
    }

    void sendActivationEmail(CompanyUser companyUser, String messageSubject, String temporalPassword, String messageBody) throws MessagingException {
        if (isProdEnvironment() || isUATEnvironment()) {
            NotificationEmail notificationEmail = new NotificationEmail();
            notificationEmail.setSubject(messageSubject);
            final String activationUrl = getActivationUrl(companyUser.getActivationKey());
            notificationEmail.setBody(messageBody + "\n\n" + activationUrl + "\n\n" + temporalPassword);
            notificationEmail.setEmailTo(companyUser.getEmail());
            EmailUtil.sendEmail(notificationEmail);
        }
    }

    String getActivationUrl(String activationKey) {
        return getEnvironment().getClientUrl() + "activate?key=" + activationKey;
    }
}
