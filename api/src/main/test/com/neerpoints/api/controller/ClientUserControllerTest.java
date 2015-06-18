package com.neerpoints.api.controller;

import com.neerpoints.service.ClientUserService;
import com.neerpoints.service.model.ClientUserLogin;
import com.neerpoints.service.model.ClientUserRegistration;
import com.neerpoints.service.model.ServiceResult;
import org.easymock.EasyMock;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ClientUserControllerTest {

    @Test
    public void testRegisterClient() throws Exception {
        final ServiceResult<Long> expectedServiceResult = new ServiceResult<>(true, "1");
        final ClientUserService clientUserService =  createClientUserServiceForRegister(expectedServiceResult);
        final ClientUserController clientUserController = new ClientUserController(clientUserService);

        ClientUserRegistration clientUserRegistration = new ClientUserRegistration();
        clientUserRegistration.setPhone("6141112233");
        ResponseEntity<ServiceResult> responseEntity = clientUserController.register(clientUserRegistration, null);
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
        final ClientUserController clientUserController = new ClientUserController(clientUserService);

        ClientUserLogin clientUserLogin = new ClientUserLogin();
        clientUserLogin.setPhone("6141232222");
        clientUserLogin.setSmsKey("qwerty");
        clientUserLogin.setEmail("a@a.com");
        clientUserLogin.setPassword("password");
        final ResponseEntity<ServiceResult> actualServiceResult = clientUserController.loginUser(clientUserLogin);
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
        ClientUserController clientUserController = new ClientUserController(clientUserService);
        ResponseEntity<ServiceResult<Boolean>> actualServiceResult = clientUserController.resendKey(new ClientUserRegistration());
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

    private ClientUserService createUserLoginService(ServiceResult<Long> serviceResult) throws Exception {
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