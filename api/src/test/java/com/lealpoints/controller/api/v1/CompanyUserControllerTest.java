package com.lealpoints.controller.api.v1;

import com.lealpoints.service.implementations.CompanyUserServiceImpl;
import com.lealpoints.service.model.CompanyUserRegistration;
import com.lealpoints.service.response.ServiceMessage;
import com.lealpoints.service.response.ServiceResult;
import org.easymock.EasyMock;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class CompanyUserControllerTest {
    @Test
    public void testRegister() throws Exception {
        final ServiceResult expectedServiceResult = new ServiceResult(false,
                new ServiceMessage("Operation not supported yet"), 1L);
        final CompanyUserRegistration companyUserRegistration = new CompanyUserRegistration();
        companyUserRegistration.setName("name");
        companyUserRegistration.setEmail("email@test.com");
        companyUserRegistration.setCompanyId(1);
        companyUserRegistration.setLanguage("es");
        final CompanyUserServiceImpl companyUserService = createCompanyUserServiceForRegister(expectedServiceResult);
        final CompanyUserController companyUserController = new CompanyUserController(companyUserService);
        ResponseEntity<ServiceResult> responseEntity = companyUserController.register(companyUserRegistration);
        assertNotNull(responseEntity);
        ServiceResult actualServiceResults = responseEntity.getBody();
        assertNotNull(actualServiceResults);
        assertEquals(expectedServiceResult.isSuccess(), actualServiceResults.isSuccess());
        assertEquals(expectedServiceResult.getMessage(), actualServiceResults.getMessage());
    }

    @Test
    public void testGet() throws Exception {
        List<String> testList = new ArrayList<>();
        testList.add("none");
        ServiceResult<List<String>> expectedServiceResult = new ServiceResult<>(false,
                new ServiceMessage("Operation not supported yet"), testList);
        ResponseEntity<ServiceResult<List<String>>> responseEntity = new CompanyUserController(null).get(1L);
        assertNotNull(responseEntity);
        ServiceResult actualServiceResults = responseEntity.getBody();
        assertNotNull(actualServiceResults);
        assertEquals(expectedServiceResult.isSuccess(), actualServiceResults.isSuccess());
        assertEquals(expectedServiceResult.getMessage(), actualServiceResults.getMessage());
        assertNotNull(actualServiceResults.getObject());
    }

    private CompanyUserServiceImpl createCompanyUserServiceForRegister(ServiceResult<String> serviceResult) throws Exception {
        final CompanyUserServiceImpl companyUserService = EasyMock.createMock(CompanyUserServiceImpl.class);
        expect(companyUserService.register((CompanyUserRegistration) anyObject())).andReturn(serviceResult).times(1);
        replay(companyUserService);
        return companyUserService;
    }
}