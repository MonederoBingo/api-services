package com.lealpoints.service.response;

import com.lealpoints.i18n.Language;
import com.lealpoints.i18n.Message;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ServiceMessageTest {

    @Test
    public void createServiceMessage_shouldReplacePlaceholders() {
        ServiceMessage serviceMessage =
                ServiceMessage.createServiceMessage(Message.MOBILE_APP_AD_MESSAGE,
                        Language.ENGLISH, "100", "Business", "http://");

        assertNotNull(serviceMessage);
        assertEquals("You've got 100 points at Business. Install Monedero Bingo to see our promotions. http://", serviceMessage.getMessage());
        assertEquals("You've got 100 points at Business. Install Monedero Bingo to see our promotions. http://",
                serviceMessage.getTranslation(Language.ENGLISH));
        assertEquals("Tienes 100 puntos en Business. Instala Monedero Bingo para ver las promociones. http://",
                serviceMessage.getTranslation(Language.SPANISH));
    }
}