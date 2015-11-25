package com.lealpoints.service.implementations;

import com.lealpoints.i18n.Message;
import com.lealpoints.service.model.ValidationResult;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PhoneValidatorServiceImplTest {

    @Test
    public void testValidate() {
        PhoneValidatorServiceImpl phoneValidatorService = new PhoneValidatorServiceImpl(null) {

            @Override
            public String getTranslation(Message message) {
                return message.name();
            }
        };
        final ValidationResult shortNumber = phoneValidatorService.validate("123");
        assertTrue(shortNumber.isInvalid());
        assertEquals(Message.PHONE_MUST_HAVE_10_DIGITS.name(), shortNumber.getMessage());

        final ValidationResult nullPhone = phoneValidatorService.validate(null);
        assertTrue(nullPhone.isInvalid());
        assertEquals(Message.PHONE_MUST_HAVE_10_DIGITS.name(), nullPhone.getMessage());

        final ValidationResult largeNumber = phoneValidatorService.validate("12345678901");
        assertTrue(largeNumber.isInvalid());
        assertEquals(Message.PHONE_MUST_HAVE_10_DIGITS.name(), largeNumber.getMessage());

        final ValidationResult validNumber = phoneValidatorService.validate("1234567890");
        assertTrue(validNumber.isValid());
        assertEquals("", validNumber.getMessage());
    }
}