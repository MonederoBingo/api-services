package com.lealpoints.util;

import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.ResourceBundle;
import com.lealpoints.context.ThreadContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Translations {

    private final ThreadContextService _threadContextService;

    @Autowired
    public Translations(ThreadContextService threadContextService) {
        _threadContextService = threadContextService;
    }

    public String getMessage(Message message) {
        String language = _threadContextService.getThreadContext().getLanguage();
        if (language == null) {
            language = "es"; //default value
        }
        String loweredLanguage = language.toLowerCase();
        if (!loweredLanguage.equalsIgnoreCase("en") || !loweredLanguage.equalsIgnoreCase("es")) {
            loweredLanguage = "es";
        }
        String country = "MX";
        if (loweredLanguage.equalsIgnoreCase("en")) {
            country = "US";
        }
        Locale locale = new Locale(loweredLanguage, country);
        ResourceBundle bundle = ResourceBundle.getBundle("UserMessage", locale);
        String val = bundle.getString(message.name());
        try {
            return new String(val.getBytes("ISO-8859-1"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return val;
        }
    }

    public static enum Message {
        COMMON_USER_ERROR,
        LOGIN_FAILED,
        WE_HAVE_SENT_YOU_AND_ACTIVATION_LINK,
        EMAIL_ALREADY_EXISTS,
        PASSWORD_AND_CONFIRMATION_ARE_DIFFERENT,
        PASSWORD_MUST_HAVE_AT_LEAST_6_CHARACTERS,
        CLIENT_REGISTERED_SUCCESSFULLY,
        PHONE_MUST_HAVE_10_DIGITS,
        THE_CLIENT_ALREADY_EXISTS,
        CONFIGURATION_UPDATED,
        PROMOTION_SUCCESSFULLY_ADDED,
        POINTS_AWARDED,
        SALE_KEY_ALREADY_EXISTS,
        CLIENT_DOES_NOT_HAVE_AVAILABLE_PROMOTIONS,
        PHONE_NUMBER_DOES_NOT_EXIST,
        COULD_NOT_READ_FILE,
        YOUR_LOGO_WAS_UPDATED,
        THE_CLIENT_DID_NOT_GET_POINTS,
        YOUR_USER_IS_NOT_ACTIVE,
        YOUR_USER_IS_ACTIVE_NOW,
        THIS_EMAIL_DOES_NOT_EXIST,
        THE_PROMOTION_WAS_DELETED,
        THE_PROMOTION_COULD_NOT_BE_DELETED,
        INVALID_LOGO_FILE,
        WELCOME_TO_LEALPOINTS_YOUR_KEY_IS,
        ACTIVATION_EMAIL_SUBJECT,
        KEY_EMAIL_SMS_MESSAGE,
        WE_HAVE_SENT_YOU_A_NEW_PASSWORD_TO_YOUR_EMAIL,
        ACTIVATION_EMAIL_BODY,
        NEW_PASSWORD_EMAIL_SUBJECT,
        YOUR_PASSWORD_HAS_BEEN_CHANGED,
        PROMOTION_APPLIED,
        NEW_PASSWORD_EMAIL_BODY,
        EMAIL_IS_EMPTY,
        PASSWORD_IS_EMPTY,
        MOBILE_APP_AD_MESSAGE,
        MOBILE_APP_AD_MESSAGE_SENT_SUCCESSFULLY,
        MOBILE_APP_AD_MESSAGE_WAS_NOT_SENT_SUCCESSFULLY,
        DEFAULT_PROMOTION_MESSAGE
    }
}
