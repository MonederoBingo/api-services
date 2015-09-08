package com.lealpoints.service.implementations;

import com.lealpoints.service.model.ValidationResult;
import com.lealpoints.util.Translations;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PhoneValidatorServiceImplTest {

    @Test
    public void testValidate() {
        PhoneValidatorServiceImpl phoneValidatorService = new PhoneValidatorServiceImpl(null, null) {

            @Override
            public String getTranslation(Translations.Message message) {
                return message.name();
            }
        };
        final ValidationResult shortNumber = phoneValidatorService.validate("123");
        assertTrue(shortNumber.isInvalid());
        assertEquals(Translations.Message.PHONE_MUST_HAVE_10_DIGITS.name(), shortNumber.getMessage());

        final ValidationResult nullPhone = phoneValidatorService.validate(null);
        assertTrue(nullPhone.isInvalid());
        assertEquals(Translations.Message.PHONE_MUST_HAVE_10_DIGITS.name(), nullPhone.getMessage());

        final ValidationResult largeNumber = phoneValidatorService.validate("12345678901");
        assertTrue(largeNumber.isInvalid());
        assertEquals(Translations.Message.PHONE_MUST_HAVE_10_DIGITS.name(), largeNumber.getMessage());

        final ValidationResult validNumber = phoneValidatorService.validate("1234567890");
        assertTrue(validNumber.isValid());
        assertEquals("", validNumber.getMessage());
    }
}