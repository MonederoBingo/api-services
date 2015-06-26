package com.neerpoints.controller.auth;

import com.neerpoints.service.CompanyService;
import com.neerpoints.service.CompanyUserService;
import com.neerpoints.service.model.CompanyRegistration;
import com.neerpoints.service.model.CompanyUserLogin;
import com.neerpoints.service.model.CompanyUserPasswordChanging;
import com.neerpoints.service.model.CompanyLoginResult;
import com.neerpoints.service.model.ServiceResult;
import org.easymock.EasyMock;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class CompanyAuthControllerTest {

    @Test
    public void testRegister() throws Exception {
        final ServiceResult expectedServiceResult = new ServiceResult(true, "1");
        final CompanyService companyService = createCompanyServiceForRegister(expectedServiceResult);
        final CompanyUserService companyUserService = createMock(CompanyUserService.class);
        final CompanyAuthController companyController = new CompanyAuthController(companyUserService, companyService);

        final CompanyRegistration companyRegistration = new CompanyRegistration();
        companyRegistration.setCompanyName("company name");
        companyRegistration.setUserName("user name");
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
        final ServiceResult<CompanyLoginResult> expectedServiceResult = new ServiceResult<>(true, "name", loginResult);
        final CompanyUserService companyUserService = createCompanyUserForLogin(expectedServiceResult);
        final CompanyService companyService = createMock(CompanyService.class);
        final CompanyAuthController companyUserController = new CompanyAuthController(companyUserService, companyService);

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
        final ServiceResult expectedServiceResult = new ServiceResult(true, "");
        CompanyUserService companyUserService = createCompanyUserForSendingActivation(expectedServiceResult);
        final CompanyService companyService = createMock(CompanyService.class);
        CompanyAuthController companyUserController = new CompanyAuthController(companyUserService, companyService);
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
        final CompanyService companyService = createMock(CompanyService.class);
        CompanyAuthController companyUserController = new CompanyAuthController(companyUserService, companyService);
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
        final CompanyService companyService = createMock(CompanyService.class);
        CompanyAuthController companyUserController = new CompanyAuthController(companyUserService, companyService);
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
        final CompanyService companyService = createMock(CompanyService.class);
        CompanyAuthController companyUserController = new CompanyAuthController(companyUserService, companyService);
        final ResponseEntity<ServiceResult> serviceResultResponseEntity = companyUserController.changePassword(new CompanyUserPasswordChanging());
        assertNotNull(serviceResultResponseEntity);
        ServiceResult actualServiceResults = serviceResultResponseEntity.getBody();
        assertNotNull(actualServiceResults);
        assertEquals(expectedServiceResult.isSuccess(), actualServiceResults.isSuccess());
        assertEquals(expectedServiceResult.getMessage(), actualServiceResults.getMessage());
        verify(companyUserService);
    }

    private CompanyUserService createCompanyUserForLogin(ServiceResult<CompanyLoginResult> serviceResult) throws Exception {
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

    private CompanyService createCompanyServiceForRegister(ServiceResult serviceResult) throws Exception {
        final CompanyService companyService = EasyMock.createMock(CompanyService.class);
        expect(companyService.register((CompanyRegistration) anyObject())).andReturn(serviceResult).times(1);
        replay(companyService);
        return companyService;
    }
}