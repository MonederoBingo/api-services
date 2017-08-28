package com.lealpoints.service.implementations;

import com.lealpoints.i18n.Message;
import com.lealpoints.model.ClientUser;
import com.lealpoints.model.CompanyUser;
import com.lealpoints.repository.ClientUserRepository;
import com.lealpoints.repository.CompanyUserRepository;
import com.lealpoints.service.response.ServiceMessage;
import com.lealpoints.service.response.ServiceResult;
import org.junit.Test;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AuthenticationServiceImplTest extends BaseServiceTest {

    @Test
    public void testIsValidCompanyApiKey() throws Exception {
        CompanyUserRepository companyUserRepository = createMock(CompanyUserRepository.class);
        expect(companyUserRepository.getByCompanyUserIdApiKey(anyInt(), anyString())).andReturn(new CompanyUser());
        replay((companyUserRepository));
        AuthenticationServiceImpl authenticationService = new AuthenticationServiceImpl(null, companyUserRepository, null) {
            @Override
            public ServiceMessage getServiceMessage(Message message, String... params) {
                return new ServiceMessage(message.name());
            }
        };

        final ServiceResult validApiKey = authenticationService.isValidApiKey(123, "123com");
        assertTrue(validApiKey.isSuccess());
        verify(companyUserRepository);
    }

    @Test
    public void testIsValidCompanyApiKeyWhenNotValid() throws Exception {
        CompanyUserRepository companyUserRepository = createMock(CompanyUserRepository.class);
        expect(companyUserRepository.getByCompanyUserIdApiKey(anyInt(), anyString())).andReturn(null);
        replay((companyUserRepository));
        AuthenticationServiceImpl authenticationService = new AuthenticationServiceImpl(null, companyUserRepository, null) {
            @Override
            public ServiceMessage getServiceMessage(Message message, String... params) {
                return new ServiceMessage(message.name());
            }
        };

        final ServiceResult validApiKey = authenticationService.isValidApiKey(123, "123com");
        assertFalse(validApiKey.isSuccess());
        verify(companyUserRepository);
    }

    @Test
    public void testIsValidCompanyApiKeyWhenWrongApiKey() throws Exception {
        AuthenticationServiceImpl authenticationService = new AuthenticationServiceImpl(null, null, null) {
            @Override
            public ServiceMessage getServiceMessage(Message message, String... params) {
                return new ServiceMessage(message.name());
            }
        };

        final ServiceResult validApiKey = authenticationService.isValidApiKey(123, "123");
        assertFalse(validApiKey.isSuccess());
    }

    @Test
    public void testIsValidClientApiKey() throws Exception {
        ClientUserRepository clientUserRepository = createMock(ClientUserRepository.class);
        expect(clientUserRepository.getByClientUserIdApiKey(anyInt(), anyString())).andReturn(
                new xyz.greatapp.libs.service.ServiceResult(true, "", new ClientUser().toJSONObject().toString()));
        replay((clientUserRepository));
        AuthenticationServiceImpl authenticationService = new AuthenticationServiceImpl(null, null, clientUserRepository) {
            @Override
            public ServiceMessage getServiceMessage(Message message, String... params) {
                return new ServiceMessage(message.name());
            }
        };

        final ServiceResult validApiKey = authenticationService.isValidApiKey(123, "123cli");
        assertTrue(validApiKey.isSuccess());
        verify(clientUserRepository);
    }


    @Test
    public void testIsValidClientApiKeyWhenNotValid() throws Exception {
        ClientUserRepository clientUserRepository = createMock(ClientUserRepository.class);
        expect(clientUserRepository.getByClientUserIdApiKey(anyInt(), anyString())).andReturn(null);
        replay((clientUserRepository));
        AuthenticationServiceImpl authenticationService = new AuthenticationServiceImpl(null, null, clientUserRepository) {
            @Override
            public ServiceMessage getServiceMessage(Message message, String... params) {
                return new ServiceMessage(message.name());
            }
        };

        final ServiceResult validApiKey = authenticationService.isValidApiKey(123, "123cli");
        assertFalse(validApiKey.isSuccess());
        verify(clientUserRepository);
    }

    @Test
    public void testIsValidClientApiKeyWhenWrongApiKey() throws Exception {
        AuthenticationServiceImpl authenticationService = new AuthenticationServiceImpl(null, null, null) {
            @Override
            public ServiceMessage getServiceMessage(Message message, String... params) {
                return new ServiceMessage(message.name());
            }
        };

        final ServiceResult validApiKey = authenticationService.isValidApiKey(123, "123");
        assertFalse(validApiKey.isSuccess());
    }
}