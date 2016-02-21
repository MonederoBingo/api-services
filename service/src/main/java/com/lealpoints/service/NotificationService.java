package com.lealpoints.service;

import com.lealpoints.model.CompanyUser;
import com.lealpoints.service.response.ServiceResult;

import javax.mail.MessagingException;

public interface NotificationService extends BaseService {

    ServiceResult sendMobileAppAdMessage(long companyId, String phone);

    void sendActivationEmail(CompanyUser companyUser, String messageSubject, String temporalPassword,
                             String messageBody) throws MessagingException;

    String getActivationUrl(String activationKey);

    String getSMSMessage(double points);

    void sendActivationEmail(String email, String activationKey) throws MessagingException;
}