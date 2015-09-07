package com.lealpoints.controller.auth;

import com.lealpoints.service.implementations.ClientUserServiceImpl;
import com.lealpoints.service.model.ClientLoginResult;
import com.lealpoints.service.model.ClientUserLogin;
import com.lealpoints.service.model.ClientUserRegistration;
import com.lealpoints.service.model.ServiceResult;
import org.easymock.EasyMock;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ClientAuthControllerTest {

    @Test
    public void testRegisterClient() throws Exception {
        final ServiceResult<String> expectedServiceResult = new ServiceResult<>(true, "1");
        final ClientUserServiceImpl clientUserService = createClientUserServiceForRegister(expectedServiceResult);
        final ClientAuthController clientAuthController = new ClientAuthController(clientUserService);

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
        final ServiceResult expectedServiceResult = new ServiceResult(true, "name");
        final ClientUserServiceImpl clientUserService = createUserLoginService(expectedServiceResult);
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
        ClientUserServiceImpl clientUserService = createUserService(expectedServiceResult);
        ClientAuthController clientAuthController = new ClientAuthController(clientUserService);
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