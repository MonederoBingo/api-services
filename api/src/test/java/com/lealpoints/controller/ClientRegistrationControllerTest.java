package com.lealpoints.controller;

import com.lealpoints.service.implementations.ClientUserServiceImpl;
import com.lealpoints.service.model.ClientLoginResult;
import com.lealpoints.service.model.ClientUserLogin;
import com.lealpoints.service.model.ClientUserRegistration;
import com.lealpoints.service.response.ServiceMessage;
import com.lealpoints.service.response.ServiceResult;
import org.easymock.EasyMock;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ClientRegistrationControllerTest {

    @Test
    public void testRegisterClient() throws Exception {
        final ServiceResult<String> expectedServiceResult = new ServiceResult<>(true, new ServiceMessage("1"));
        final ClientUserServiceImpl clientUserService = createClientUserServiceForRegister(expectedServiceResult);
        final ClientAuthenticationController clientAuthController = new ClientAuthenticationController(clientUserService);

        ClientUserRegistration clientUserRegistration = new ClientUserRegistration();
        clientUserRegistration.setPhone("6141112233");
        ResponseEntity<ServiceResult<String>> responseEntity = clientAuthController.register(clientUserRegistration, null);
        assertNotNull(responseEntity);
        ServiceResult actualServiceResults = responseEntity.getBody();
        assertNotNull(actualServiceResults);
        assertEquals(expectedServiceResult.isSuccess(), actualServiceResults.isSuccess());
        assertEquals(expectedServiceResult.getMessage(), actualServiceResults.getMessage());
        verify(clientUserService);
    }

    @Test
    public void testLoginUser() throws Exception {
        final ServiceResult expectedServiceResult = new ServiceResult(true, new ServiceMessage("name"));
        final ClientUserServiceImpl clientUserService = createUserLoginService(expectedServiceResult);
        final ClientAuthenticationController clientAuthController = new ClientAuthenticationController(clientUserService);

        ClientUserLogin clientUserLogin = new ClientUserLogin();
        clientUserLogin.setPhoneNumber("6141232222");
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
        ServiceResult<Boolean> expectedServiceResult = new ServiceResult<>(true, ServiceMessage.EMPTY, true);
        ClientUserServiceImpl clientUserService = createUserService(expectedServiceResult);
        ClientAuthenticationController clientAuthController = new ClientAuthenticationController(clientUserService);
        ResponseEntity<ServiceResult<Boolean>> actualServiceResult = clientAuthController.resendKey(new ClientUserRegistration());
        assertNotNull(actualServiceResult);
        ServiceResult actualServiceResults = actualServiceResult.getBody();
        assertNotNull(actualServiceResults);
        assertEquals(expectedServiceResult.isSuccess(), actualServiceResults.isSuccess());
        assertEquals(expectedServiceResult.getMessage(), actualServiceResults.getMessage());
        verify(clientUserService);
    }

    private ClientUserServiceImpl createUserService(ServiceResult<Boolean> serviceResult) throws Exception {
        ClientUserServiceImpl clientUserService = createMock(ClientUserServiceImpl.class);
        expect((clientUserService.resendKey(anyString()))).andReturn(serviceResult);
        replay(clientUserService);
        return clientUserService;
    }

    private ClientUserServiceImpl createUserLoginService(ServiceResult<ClientLoginResult> serviceResult) throws Exception {
        ClientUserServiceImpl clientUserService = EasyMock.createMock(ClientUserServiceImpl.class);
        expect(clientUserService.login((ClientUserLogin) anyObject())).andReturn(serviceResult);
        replay(clientUserService);
        return clientUserService;
    }

    private ClientUserServiceImpl createClientUserServiceForRegister(ServiceResult<String> expectedServiceResult) throws Exception {
        final ClientUserServiceImpl clientUserService = EasyMock.createMock(ClientUserServiceImpl.class);
        expect(clientUserService.register((ClientUserRegistration) anyObject())).andReturn(expectedServiceResult);
        replay(clientUserService);
        return clientUserService;
    }
}