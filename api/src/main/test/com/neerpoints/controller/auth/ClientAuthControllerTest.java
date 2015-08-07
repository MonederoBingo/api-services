package com.neerpoints.controller.auth;

import com.neerpoints.service.ClientUserService;
import com.neerpoints.service.model.ClientLoginResult;
import com.neerpoints.service.model.ClientUserLogin;
import com.neerpoints.service.model.ClientUserRegistration;
import com.neerpoints.service.model.ServiceResult;
import org.easymock.EasyMock;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ClientAuthControllerTest {

    @Test
    public void testRegisterClient() throws Exception {
        final ServiceResult<Long> expectedServiceResult = new ServiceResult<>(true, "1");
        final ClientUserService clientUserService =  createClientUserServiceForRegister(expectedServiceResult);
        final ClientAuthController clientAuthController = new ClientAuthController(clientUserService);

        ClientUserRegistration clientUserRegistration = new ClientUserRegistration();
        clientUserRegistration.setPhone("6141112233");
        ResponseEntity<ServiceResult> responseEntity = clientAuthController.register(clientUserRegistration, null);
        assertNotNull(responseEntity);
        ServiceResult actualServiceResults = responseEntity.getBody();
        assertNotNull(actualServiceResults);
        assertEquals(expectedServiceResult.isSuccess(), actualServiceResults.isSuccess());
        assertEquals(expectedServiceResult.getMessage(), actualServiceResults.getMessage());
        verify(clientUserService);
    }

    @Test
    public void testLoginUser() throws Exception {
        final ServiceResult expectedServiceResult = new ServiceResult(true, "name");
        final ClientUserService clientUserService = createUserLoginService(expectedServiceResult);
        final ClientAuthController clientAuthController = new ClientAuthController(clientUserService);

        ClientUserLogin clientUserLogin = new ClientUserLogin();
        clientUserLogin.setPhone("6141232222");
        clientUserLogin.setSmsKey("qwerty");
        clientUserLogin.setEmail("a@a.com");
        clientUserLogin.setPassword("password");
        final ResponseEntity<ServiceResult<ClientLoginResult>> actualServiceResult = clientAuthController.loginUser(clientUserLogin);
        assertNotNull(actualServiceResult);
        ServiceResult actualServiceResults = actualServiceResult.getBody();
        assertNotNull(actualServiceResults);
        assertEquals(expectedServiceResult.isSuccess(), actualServiceResults.isSuccess());
        assertEquals(expectedServiceResult.getMessage(), actualServiceResults.getMessage());
        verify(clientUserService);
    }

    @Test
    public void testResendKey() throws Exception {
        ServiceResult<Boolean> expectedServiceResult = new ServiceResult<>(true, "", true);
        ClientUserService clientUserService = createUserService(expectedServiceResult);
        ClientAuthController clientAuthController = new ClientAuthController(clientUserService);
        ResponseEntity<ServiceResult<Boolean>> actualServiceResult = clientAuthController.resendKey(new ClientUserRegistration());
        assertNotNull(actualServiceResult);
        ServiceResult actualServiceResults = actualServiceResult.getBody();
        assertNotNull(actualServiceResults);
        assertEquals(expectedServiceResult.isSuccess(), actualServiceResults.isSuccess());
        assertEquals(expectedServiceResult.getMessage(), actualServiceResults.getMessage());
        verify(clientUserService);
    }

    private ClientUserService createUserService(ServiceResult<Boolean> serviceResult) throws Exception {
        ClientUserService clientUserService = createMock(ClientUserService.class);
        expect((clientUserService.resendKey(anyString()))).andReturn(serviceResult);
        replay(clientUserService);
        return clientUserService;
    }

    private ClientUserService createUserLoginService(ServiceResult<ClientLoginResult> serviceResult) throws Exception {
        ClientUserService clientUserService = EasyMock.createMock(ClientUserService.class);
        expect(clientUserService.login((ClientUserLogin) anyObject())).andReturn(serviceResult);
        replay(clientUserService);
        return clientUserService;
    }

    private ClientUserService createClientUserServiceForRegister(ServiceResult<Long> expectedServiceResult) throws Exception {
        final ClientUserService clientUserService = EasyMock.createMock(ClientUserService.class);
        expect(clientUserService.register((ClientUserRegistration) anyObject())).andReturn(expectedServiceResult);
        replay(clientUserService);
        return clientUserService;
    }
}