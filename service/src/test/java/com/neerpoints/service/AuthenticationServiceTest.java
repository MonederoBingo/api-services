package com.neerpoints.service;

import com.neerpoints.model.CompanyUser;
import com.neerpoints.repository.CompanyUserRepository;
import com.neerpoints.service.model.ServiceResult;
import com.neerpoints.util.Translations;
import org.junit.Test;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class AuthenticationServiceTest {


    @Test
    public void testIsValidApiKey() throws Exception {
        CompanyUserRepository companyUserRepository = createMock(CompanyUserRepository.class);
        expect(companyUserRepository.getByCompanyUserIdApiKey(anyString(), anyString())).andReturn(new CompanyUser());
        replay((companyUserRepository));
        AuthenticationService authenticationService = new AuthenticationService(null, null, companyUserRepository){
            @Override
            String getTranslation(Translations.Message message) {
                return message.name();
            }
        };

        final ServiceResult validApiKey = authenticationService.isValidApiKey("ABC", "123");
        assertTrue(validApiKey.isSuccess());
        verify(companyUserRepository);
    }

    @Test
    public void testIsValidApiKeyWhenNotValid() throws Exception {
        CompanyUserRepository companyUserRepository = createMock(CompanyUserRepository.class);
        expect(companyUserRepository.getByCompanyUserIdApiKey(anyString(), anyString())).andReturn(null);
        replay((companyUserRepository));
        AuthenticationService authenticationService = new AuthenticationService(null, null, companyUserRepository){
            @Override
            String getTranslation(Translations.Message message) {
                return message.name();
            }
        };

        final ServiceResult validApiKey = authenticationService.isValidApiKey("ABC", "123");
        assertFalse(validApiKey.isSuccess());
        verify(companyUserRepository);
    }

}