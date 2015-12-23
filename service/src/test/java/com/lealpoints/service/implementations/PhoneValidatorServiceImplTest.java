package com.lealpoints.service.implementations;

import com.lealpoints.i18n.Message;
import com.lealpoints.service.model.ValidationResult;
import com.lealpoints.service.response.ServiceMessage;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PhoneValidatorServiceImplTest {

    @Test
    public void testValidate() {
        PhoneValidatorServiceImpl phoneValidatorService = new PhoneValidatorServiceImpl(null) {

            @Override
            public ServiceMessage getServiceMessage(Message message, String... params) {
                return new ServiceMessage(message.name());
            }
        };
        final ValidationResult shortNumber = phoneValidatorService.validate("123");
        assertTrue(shortNumber.isInvalid());
        assertEquals(Message.PHONE_MUST_HAVE_10_DIGITS.name(), shortNumber.getServiceMessage().getMessage());

        final ValidationResult nullPhone = phoneValidatorService.validate(null);
        assertTrue(nullPhone.isInvalid());
        assertEquals(Message.PHONE_MUST_HAVE_10_DIGITS.name(), nullPhone.getServiceMessage().getMessage());

        final ValidationResult largeNumber = phoneValidatorService.validate("12345678901");
        assertTrue(largeNumber.isInvalid());
        assertEquals(Message.PHONE_MUST_HAVE_10_DIGITS.name(), largeNumber.getServiceMessage().getMessage());

        final ValidationResult validNumber = phoneValidatorService.validate("1234567890");
        assertTrue(validNumber.isValid());
        assertEquals("", validNumber.getServiceMessage().getMessage());
    }
}