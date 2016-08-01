package com.monederobingo.controller;

import com.lealpoints.service.implementations.CompanyServiceImpl;
import com.lealpoints.service.implementations.CompanyUserServiceImpl;
import com.lealpoints.service.model.CompanyLoginResult;
import com.lealpoints.service.model.CompanyRegistration;
import com.lealpoints.service.model.CompanyUserLogin;
import com.lealpoints.service.model.CompanyUserPasswordChanging;
import com.lealpoints.service.response.ServiceMessage;
import com.lealpoints.service.response.ServiceResult;
import org.easymock.EasyMock;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class CompanyAuthenticationControllerTest {

    @Test
    public void testRegister() throws Exception {
        final ServiceResult expectedServiceResult = new ServiceResult(true, new ServiceMessage("1"));
        final CompanyServiceImpl companyService = createCompanyServiceForRegister(expectedServiceResult);
        final CompanyUserServiceImpl companyUserService = createMock(CompanyUserServiceImpl.class);
        final CompanyAuthenticationController companyController = new CompanyAuthenticationController(companyUserService, companyService);

        final CompanyRegistration companyRegistration = new CompanyRegistration();
        companyRegistration.setCompanyName("company name");
        companyRegistration.setUsername("user name");
        companyRegistration.setEmail("email@test.com");
        companyRegistration.setPassword("Pa$$w0rd");
        companyRegistration.setUrlImageLogo("images/logo.png");

        ResponseEntity<ServiceResult> responseEntity = companyController.register(companyRegistration);
        assertNotNull(responseEntity);
        ServiceResult actualServiceResults = responseEntity.getBody();
        assertNotNull(actualServiceResults);
        assertEquals(expectedServiceResult.isSuccess(), actualServiceResults.isSuccess());
        assertEquals(expectedServiceResult.getMessage(), actualServiceResults.getMessage());
        verify(companyService);
    }

    @Test
    public void testLoginUser() throws Exception {
        CompanyLoginResult loginResult = new CompanyLoginResult();
        loginResult.setActive(true);
        final ServiceResult<CompanyLoginResult> expectedServiceResult = new ServiceResult<>(true, new ServiceMessage("name"), loginResult);
        final CompanyUserServiceImpl companyUserService = createCompanyUserForLogin(expectedServiceResult);
        final CompanyServiceImpl companyService = createMock(CompanyServiceImpl.class);
        final CompanyAuthenticationController companyUserController = new CompanyAuthenticationController(companyUserService, companyService);

        CompanyUserLogin companyUserLogin = new CompanyUserLogin();
        companyUserLogin.setEmail("a@a.com");
        companyUserLogin.setPassword("password");
        final ResponseEntity<ServiceResult<CompanyLoginResult>> actualServiceResult = companyUserController.login(companyUserLogin);
        assertNotNull(actualServiceResult);
        ServiceResult actualServiceResults = actualServiceResult.getBody();
        assertNotNull(actualServiceResults);
        assertEquals(expectedServiceResult.isSuccess(), actualServiceResults.isSuccess());
        assertEquals(expectedServiceResult.getMessage(), actualServiceResults.getMessage());
        assertNotNull(expectedServiceResult.getObject());
        CompanyLoginResult expectedLoginResult = expectedServiceResult.getObject();
        assertTrue(expectedLoginResult.isActive());
        verify(companyUserService);
    }

    @Test
    public void testSendActivationEmail() throws Exception {
        final ServiceResult expectedServiceResult = new ServiceResult(true, ServiceMessage.EMPTY);
        CompanyUserServiceImpl companyUserService = createCompanyUserForSendingActivation(expectedServiceResult);
        final CompanyServiceImpl companyService = createMock(CompanyServiceImpl.class);
        CompanyAuthenticationController companyUserController = new CompanyAuthenticationController(companyUserService, companyService);
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
        final ServiceResult expectedServiceResult = new ServiceResult(true, ServiceMessage.EMPTY);
        CompanyUserServiceImpl companyUserService = createCompanyUserForActivate(expectedServiceResult);
        final CompanyServiceImpl companyService = createMock(CompanyServiceImpl.class);
        CompanyAuthenticationController companyUserController = new CompanyAuthenticationController(companyUserService, companyService);
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
        final ServiceResult expectedServiceResult = new ServiceResult(true, ServiceMessage.EMPTY);
        CompanyUserServiceImpl companyUserService = createCompanyUserForSendingTempPassword(expectedServiceResult);
        final CompanyServiceImpl companyService = createMock(CompanyServiceImpl.class);
        CompanyAuthenticationController companyUserController = new CompanyAuthenticationController(companyUserService, companyService);
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
        final ServiceResult expectedServiceResult = new ServiceResult(true, ServiceMessage.EMPTY);
        CompanyUserServiceImpl companyUserService = createCompanyUserForChangingPassword(expectedServiceResult);
        final CompanyServiceImpl companyService = createMock(CompanyServiceImpl.class);
        CompanyAuthenticationController companyUserController = new CompanyAuthenticationController(companyUserService, companyService);
        final ResponseEntity<ServiceResult> serviceResultResponseEntity = companyUserController.changePassword(new CompanyUserPasswordChanging());
        assertNotNull(serviceResultResponseEntity);
        ServiceResult actualServiceResults = serviceResultResponseEntity.getBody();
        assertNotNull(actualServiceResults);
        assertEquals(expectedServiceResult.isSuccess(), actualServiceResults.isSuccess());
        assertEquals(expectedServiceResult.getMessage(), actualServiceResults.getMessage());
        verify(companyUserService);
    }

    private CompanyUserServiceImpl createCompanyUserForLogin(ServiceResult<CompanyLoginResult> serviceResult) throws Exception {
        CompanyUserServiceImpl companyUserService = EasyMock.createMock(CompanyUserServiceImpl.class);
        expect(companyUserService.loginUser((CompanyUserLogin) anyObject())).andReturn(serviceResult);
        replay(companyUserService);
        return companyUserService;
    }

    private CompanyUserServiceImpl createCompanyUserForSendingActivation(ServiceResult serviceResult) throws Exception {
        CompanyUserServiceImpl companyUserService = EasyMock.createMock(CompanyUserServiceImpl.class);
        expect(companyUserService.sendActivationEmail(anyString())).andReturn(serviceResult);
        replay(companyUserService);
        return companyUserService;
    }

    private CompanyUserServiceImpl createCompanyUserForSendingTempPassword(ServiceResult serviceResult) throws Exception {
        CompanyUserServiceImpl companyUserService = EasyMock.createMock(CompanyUserServiceImpl.class);
        expect(companyUserService.sendTempPasswordEmail(anyString())).andReturn(serviceResult);
        replay(companyUserService);
        return companyUserService;
    }

    private CompanyUserServiceImpl createCompanyUserForChangingPassword(ServiceResult serviceResult) throws Exception {
        CompanyUserServiceImpl companyUserService = EasyMock.createMock(CompanyUserServiceImpl.class);
        expect(companyUserService.changePassword(EasyMock.<CompanyUserPasswordChanging>anyObject())).andReturn(serviceResult);
        replay(companyUserService);
        return companyUserService;
    }

    private CompanyUserServiceImpl createCompanyUserForActivate(ServiceResult serviceResult) throws Exception {
        CompanyUserServiceImpl companyUserService = EasyMock.createMock(CompanyUserServiceImpl.class);
        expect(companyUserService.activateUser(anyString())).andReturn(serviceResult);
        replay(companyUserService);
        return companyUserService;
    }

    private CompanyServiceImpl createCompanyServiceForRegister(ServiceResult serviceResult) throws Exception {
        final CompanyServiceImpl companyService = EasyMock.createMock(CompanyServiceImpl.class);
        expect(companyService.register((CompanyRegistration) anyObject())).andReturn(serviceResult).times(1);
        replay(companyService);
        return companyService;
    }
}
