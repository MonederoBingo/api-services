package com.lealpoints.service.implementations;

import com.lealpoints.context.ThreadContextService;
import com.lealpoints.i18n.Message;
import com.lealpoints.model.Company;
import com.lealpoints.model.CompanyUser;
import com.lealpoints.model.NotificationEmail;
import com.lealpoints.repository.ClientRepository;
import com.lealpoints.repository.CompanyClientMappingRepository;
import com.lealpoints.repository.CompanyRepository;
import com.lealpoints.service.NotificationService;
import com.lealpoints.service.response.ServiceResult;
import com.lealpoints.util.EmailUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.text.DecimalFormat;

@Component
public class NotificationServiceImpl extends BaseServiceImpl implements NotificationService {
    private static final Logger logger = LogManager.getLogger(NotificationServiceImpl.class.getName());
    private CompanyRepository companyRepository;
    private ClientRepository clientRepository;
    private CompanyClientMappingRepository companyClientMappingRepository;
    private final SMSServiceImpl _smsService;

    @Autowired
    public NotificationServiceImpl(ThreadContextService threadContextService, CompanyRepository companyRepository,
                                   ClientRepository clientRepository, CompanyClientMappingRepository companyClientMappingRepository, SMSServiceImpl smsService) {
        super(threadContextService);
        this.companyRepository = companyRepository;
        this.clientRepository = clientRepository;
        this.companyClientMappingRepository = companyClientMappingRepository;
        _smsService = smsService;
    }

    public ServiceResult sendMobileAppAdMessage(long companyId, String phone) {
        try {
            final Company company = companyRepository.getByCompanyId(companyId);
            long clientId = clientRepository.getByPhone(phone).getClientId();
            final double points = companyClientMappingRepository.getByCompanyIdClientId(companyId, clientId).getPoints();
            if (company != null) {
                final String smsMessage = getSMSMessage(company.getName(), points);
                logger.info("Promo SMS sent to: " + phone);
                _smsService.sendSMSMessage(phone, smsMessage);
                clientRepository.updateCanReceivePromoSms(clientId, false);
                ServiceResult<Integer> serviceResult = new ServiceResult<>(true,
                        getServiceMessage(Message.MOBILE_APP_AD_MESSAGE_SENT_SUCCESSFULLY));
                serviceResult.setExtraInfo(smsMessage);
                return serviceResult;
            } else {
                logger.error("None company has the companyId: " + companyId);
                return new ServiceResult<>(false, getServiceMessage(Message.COMMON_USER_ERROR));
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return new ServiceResult(false, getServiceMessage(Message.MOBILE_APP_AD_MESSAGE_WAS_NOT_SENT_SUCCESSFULLY));
        }
    }

    public void sendActivationEmail(CompanyUser companyUser, String messageSubject, String temporalPassword, String messageBody) throws MessagingException {
        if (isProdEnvironment() || isUATEnvironment()) {
            NotificationEmail notificationEmail = new NotificationEmail();
            notificationEmail.setSubject(messageSubject);
            final String activationUrl = getActivationUrl(companyUser.getActivationKey());
            notificationEmail.setBody(messageBody + "\n\n" + activationUrl + "\n\n" + temporalPassword);
            notificationEmail.setEmailTo(companyUser.getEmail());
            EmailUtil.sendEmail(notificationEmail);
        }
    }

    public String getActivationUrl(String activationKey) {
        return getEnvironment().getClientUrl() + "activate?key=" + activationKey;
    }

    public String getSMSMessage(String companyName, double points) {
        final String appUrl = "https://goo.gl/JRssA6";
        final String translation = getServiceMessage(Message.MOBILE_APP_AD_MESSAGE, String.valueOf(points), "", appUrl).getMessage();
        final int SMS_MESSAGE_MAX_CHAR = 160;
        final int formattedMessageLength = String.format(translation, points, "", appUrl).length();
        if (formattedMessageLength >= SMS_MESSAGE_MAX_CHAR) {
            throw new IllegalArgumentException("Message length must be less than " + SMS_MESSAGE_MAX_CHAR + " in: " + translation);
        }
        final int maxCompanyNameLength = SMS_MESSAGE_MAX_CHAR - formattedMessageLength;
        String trimmedCompanyName = "";
        if (maxCompanyNameLength > 0) {
            trimmedCompanyName = StringUtils.abbreviate(companyName, Math.max(Math.min(maxCompanyNameLength, companyName.length()), 4));
        }
        return String.format(translation, new DecimalFormat("#.#").format(points), trimmedCompanyName, appUrl);
    }

    public void sendActivationEmail(String email, String activationKey) throws MessagingException {
        if (isProdEnvironment() || isUATEnvironment()) {
            NotificationEmail notificationEmail = new NotificationEmail();
            notificationEmail.setSubject(getServiceMessage(Message.ACTIVATION_EMAIL_SUBJECT).getMessage());
            final String activationUrl = getActivationUrl(activationKey);
            notificationEmail.setBody(getServiceMessage(Message.ACTIVATION_EMAIL_BODY) + "\n\n" + activationUrl);
            notificationEmail.setEmailTo(email);
            EmailUtil.sendEmail(notificationEmail);
        }
    }
}
