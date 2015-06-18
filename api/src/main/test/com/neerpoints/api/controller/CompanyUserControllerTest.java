package com.neerpoints.api.controller;

import com.neerpoints.service.CompanyUserService;
import com.neerpoints.service.model.CompanyUserLogin;
import com.neerpoints.service.model.CompanyUserPasswordChanging;
import com.neerpoints.service.model.LoginResult;
import com.neerpoints.service.model.ServiceResult;
import org.easymock.EasyMock;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class CompanyUserControllerTest {

    @Test
    public void testLoginUser() throws Exception {
        LoginResult loginResult = new LoginResult();
        loginResult.setActive(true);
        final ServiceResult<LoginResult> expectedServiceResult = new ServiceResult<>(true, "name", loginResult);
        final CompanyUserService companyUserService = createCompanyUserForLogin(expectedServiceResult);
        final CompanyUserController companyUserController = new CompanyUserController(companyUserService);

        CompanyUserLogin companyUserLogin = new CompanyUserLogin();
        companyUserLogin.setEmail("a@a.com");
        companyUserLogin.setPassword("password");
        final ResponseEntity<ServiceResult<LoginResult>> actualServiceResult = companyUserController.login(companyUserLogin);
        assertNotNull(actualServiceResult);
        ServiceResult actualServiceResults = actualServiceResult.getBody();
        assertNotNull(actualServiceResults);
        assertEquals(expectedServiceResult.isSuccess(), actualServiceResults.isSuccess());
        assertEquals(expectedServiceResult.getMessage(), actualServiceResults.getMessage());
        assertNotNull(expectedServiceResult.getObject());
        LoginResult expectedLoginResult = expectedServiceResult.getObject();
        assertTrue(expectedLoginResult.isActive());
        verify(companyUserService);
    }

    @Test
    public void testSendActivationEmail() throws Exception {
        final ServiceResult expectedServiceResult = new ServiceResult(true, "");
        CompanyUserService companyUserService = createCompanyUserForSendingActivation(expectedServiceResult);
        CompanyUserController companyUserController = new CompanyUserController(companyUserService);
        final ResponseEntity<ServiceResult> serviceResultResponseEntity = companyUserController.sendActivationEmail("");
        assertNotNull(serviceResultResponseEntity);
        ServiceResult actualServiceResults = serviceResultResponseEntity.getBody();
        assertNotNull(actualServiceResults);
        assertEquals(expectedServiceResult.isSuccess(), actualServiceResults.isSuccess());
        assertEquals(expectedServiceResult.getMessage(), actualServiceResults.getMessage());
        verify(companyUserService);
    }

    @Test
    public void testActivate() throws Exception {
        final ServiceResult expectedServiceResult = new ServiceResult(true, "");
        CompanyUserService companyUserService = createCompanyUserForActivate(expectedServiceResult);
        CompanyUserController companyUserController = new CompanyUserController(companyUserService);
        final ResponseEntity<ServiceResult> serviceResultResponseEntity = companyUserController.activate("");
        assertNotNull(serviceResultResponseEntity);
        ServiceResult actualServiceResults = serviceResultResponseEntity.getBody();
        assertNotNull(actualServiceResults);
        assertEquals(expectedServiceResult.isSuccess(), actualServiceResults.isSuccess());
        assertEquals(expectedServiceResult.getMessage(), actualServiceResults.getMessage());
        verify(companyUserService);
    }

    @Test
    public void testSendTempPasswordEmail() throws Exception {
        final ServiceResult expectedServiceResult = new ServiceResult(true, "");
        CompanyUserService companyUserService = createCompanyUserForSendingTempPassword(expectedServiceResult);
        CompanyUserController companyUserController = new CompanyUserController(companyUserService);
        final ResponseEntity<ServiceResult> serviceResultResponseEntity = companyUserController.sendTempPasswordEmail("");
        assertNotNull(serviceResultResponseEntity);
        ServiceResult actualServiceResults = serviceResultResponseEntity.getBody();
        assertNotNull(actualServiceResults);
        assertEquals(expectedServiceResult.isSuccess(), actualServiceResults.isSuccess());
        assertEquals(expectedServiceResult.getMessage(), actualServiceResults.getMessage());
        verify(companyUserService);
    }

    @Test
    public void testChangePassword() throws Exception {
        final ServiceResult expectedServiceResult = new ServiceResult(true, "");
        CompanyUserService companyUserService = createCompanyUserForChangingPassword(expectedServiceResult);
        CompanyUserController companyUserController = new CompanyUserController(companyUserService);
        final ResponseEntity<ServiceResult> serviceResultResponseEntity = companyUserController.changePassword(new CompanyUserPasswordChanging());
        assertNotNull(serviceResultResponseEntity);
        ServiceResult actualServiceResults = serviceResultResponseEntity.getBody();
        assertNotNull(actualServiceResults);
        assertEquals(expectedServiceResult.isSuccess(), actualServiceResults.isSuccess());
        assertEquals(expectedServiceResult.getMessage(), actualServiceResults.getMessage());
        verify(companyUserService);
    }

    private CompanyUserService createCompanyUserForLogin(ServiceResult<LoginResult> serviceResult) throws Exception {
        CompanyUserService companyUserService = EasyMock.createMock(CompanyUserService.class);
        expect(companyUserService.loginUser((CompanyUserLogin) anyObject())).andReturn(serviceResult);
        replay(companyUserService);
        return companyUserService;
    }

    private CompanyUserService createCompanyUserForSendingActivation(ServiceResult serviceResult) throws Exception {
        CompanyUserService companyUserService = EasyMock.createMock(CompanyUserService.class);
        expect(companyUserService.sendActivationEmail(anyString())).andReturn(serviceResult);
        replay(companyUserService);
        return companyUserService;
    }

    private CompanyUserService createCompanyUserForSendingTempPassword(ServiceResult serviceResult) throws Exception {
        CompanyUserService companyUserService = EasyMock.createMock(CompanyUserService.class);
        expect(companyUserService.sendTempPasswordEmail(anyString())).andReturn(serviceResult);
        replay(companyUserService);
        return companyUserService;
    }

    private CompanyUserService createCompanyUserForChangingPassword(ServiceResult serviceResult) throws Exception {
        CompanyUserService companyUserService = EasyMock.createMock(CompanyUserService.class);
        expect(companyUserService.changePassword(EasyMock.<CompanyUserPasswordChanging>anyObject())).andReturn(serviceResult);
        replay(companyUserService);
        return companyUserService;
    }

    private CompanyUserService createCompanyUserForActivate(ServiceResult serviceResult) throws Exception {
        CompanyUserService companyUserService = EasyMock.createMock(CompanyUserService.class);
        expect(companyUserService.activateUser(anyString())).andReturn(serviceResult);
        replay(companyUserService);
        return companyUserService;
    }
}