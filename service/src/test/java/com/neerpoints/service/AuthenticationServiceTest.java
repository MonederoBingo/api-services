package com.neerpoints.service;

import com.neerpoints.model.ClientUser;
import com.neerpoints.model.CompanyUser;
import com.neerpoints.repository.ClientUserRepository;
import com.neerpoints.repository.CompanyUserRepository;
import com.neerpoints.service.model.ServiceResult;
import com.neerpoints.util.Translations;
import org.junit.Test;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AuthenticationServiceTest {

    @Test
    public void testIsValidCompanyApiKey() throws Exception {
        CompanyUserRepository companyUserRepository = createMock(CompanyUserRepository.class);
        expect(companyUserRepository.getByCompanyUserIdApiKey(anyString(), anyString())).andReturn(new CompanyUser());
        replay((companyUserRepository));
        AuthenticationService authenticationService = new AuthenticationService(null, null, companyUserRepository, null){
            @Override
            String getTranslation(Translations.Message message) {
                return message.name();
            }
        };

        final ServiceResult validApiKey = authenticationService.isValidApiKey("ABC", "123com");
        assertTrue(validApiKey.isSuccess());
        verify(companyUserRepository);
    }

    @Test
    public void testIsValidCompanyApiKeyWhenNotValid() throws Exception {
        CompanyUserRepository companyUserRepository = createMock(CompanyUserRepository.class);
        expect(companyUserRepository.getByCompanyUserIdApiKey(anyString(), anyString())).andReturn(null);
        replay((companyUserRepository));
        AuthenticationService authenticationService = new AuthenticationService(null, null, companyUserRepository, null){
            @Override
            String getTranslation(Translations.Message message) {
                return message.name();
            }
        };

        final ServiceResult validApiKey = authenticationService.isValidApiKey("ABC", "123com");
        assertFalse(validApiKey.isSuccess());
        verify(companyUserRepository);
    }

    @Test
    public void testIsValidCompanyApiKeyWhenWrongApiKey() throws Exception {
        AuthenticationService authenticationService = new AuthenticationService(null, null, null, null){
            @Override
            String getTranslation(Translations.Message message) {
                return message.name();
            }
        };

        final ServiceResult validApiKey = authenticationService.isValidApiKey("ABC", "123");
        assertFalse(validApiKey.isSuccess());
    }

    @Test
    public void testIsValidClientApiKey() throws Exception {
        ClientUserRepository clientUserRepository = createMock(ClientUserRepository.class);
        expect(clientUserRepository.getByClientUserIdApiKey(anyString(), anyString())).andReturn(new ClientUser());
        replay((clientUserRepository));
        AuthenticationService authenticationService = new AuthenticationService(null, null, null, clientUserRepository){
            @Override
            String getTranslation(Translations.Message message) {
                return message.name();
            }
        };

        final ServiceResult validApiKey = authenticationService.isValidApiKey("ABC", "123cli");
        assertTrue(validApiKey.isSuccess());
        verify(clientUserRepository);
    }


    @Test
    public void testIsValidClientApiKeyWhenNotValid() throws Exception {
        ClientUserRepository clientUserRepository = createMock(ClientUserRepository.class);
        expect(clientUserRepository.getByClientUserIdApiKey(anyString(), anyString())).andReturn(null);
        replay((clientUserRepository));
        AuthenticationService authenticationService = new AuthenticationService(null, null, null, clientUserRepository){
            @Override
            String getTranslation(Translations.Message message) {
                return message.name();
            }
        };

        final ServiceResult validApiKey = authenticationService.isValidApiKey("ABC", "123cli");
        assertFalse(validApiKey.isSuccess());
        verify(clientUserRepository);
    }

    @Test
    public void testIsValidClientApiKeyWhenWrongApiKey() throws Exception {
        AuthenticationService authenticationService = new AuthenticationService(null, null, null, null){
            @Override
            String getTranslation(Translations.Message message) {
                return message.name();
            }
        };

        final ServiceResult validApiKey = authenticationService.isValidApiKey("ABC", "123");
        assertFalse(validApiKey.isSuccess());
    }
}