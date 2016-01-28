package com.lealpoints.controller.api.v1;

import com.lealpoints.model.CompanyUser;
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CompanyUserControllerTest {
    @Test
    public void testRegister() throws Exception {
        final ServiceResult expectedServiceResult = new ServiceResult<>(false,
                new ServiceMessage("Operation not supported yet"));
        final CompanyUserRegistration companyUserRegistration = new CompanyUserRegistration();
        companyUserRegistration.setName("name");
        companyUserRegistration.setEmail("email@test.com");
        companyUserRegistration.setCompanyId(1);
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
        List<CompanyUser> expectedUsers = new ArrayList<>();
        expectedUsers.add(createCompanyUser("fernando", "fernando@monederobingo.com"));
        expectedUsers.add(createCompanyUser("alonso", "alonso@monederobingo.com"));
        final ServiceResult<List<CompanyUser>> expectedServiceResult = new ServiceResult<>
                (true, new ServiceMessage("1"), expectedUsers);
        final CompanyUserServiceImpl companyUserService = createCompanyUserServiceForGet(expectedServiceResult);
        final CompanyUserController companyUserController = new CompanyUserController(companyUserService);

        ResponseEntity<ServiceResult<List<CompanyUser>>> responseEntity = companyUserController.get(1L);
        assertNotNull(responseEntity);
        ServiceResult actualServiceResults = responseEntity.getBody();
        assertNotNull(actualServiceResults);
        assertEquals(expectedServiceResult.isSuccess(), actualServiceResults.isSuccess());
        assertEquals(expectedServiceResult.getMessage(), actualServiceResults.getMessage());
        assertNotNull(expectedServiceResult.getObject());
        List<CompanyUser> companyUser = expectedServiceResult.getObject();
        assertEquals(2, companyUser.size());
        assertEquals("fernando", companyUser.get(0).getName());
        assertEquals("fernando@monederobingo.com", companyUser.get(0).getEmail());
        assertEquals("alonso", companyUser.get(1).getName());
        assertEquals("alonso@monederobingo.com", companyUser.get(1).getEmail());
    }

    private CompanyUserServiceImpl createCompanyUserServiceForGet(ServiceResult<List<CompanyUser>> serviceResult) throws Exception {
        final CompanyUserServiceImpl companyUserService = EasyMock.createMock(CompanyUserServiceImpl.class);
        expect(companyUserService.getByCompanyId(anyLong())).andReturn(serviceResult);
        replay(companyUserService);
        return companyUserService;
    }

    private CompanyUser createCompanyUser(String name, String email) {
        CompanyUser companyUsers = new CompanyUser();
        companyUsers.setName(name);
        companyUsers.setEmail(email);
        return companyUsers;
    }

    private CompanyUserServiceImpl createCompanyUserServiceForRegister(ServiceResult<String> serviceResult) throws Exception {
        final CompanyUserServiceImpl companyUserService = EasyMock.createMock(CompanyUserServiceImpl.class);
        expect(companyUserService.register((CompanyUserRegistration) anyObject())).andReturn(serviceResult).times(1);
        replay(companyUserService);
        return companyUserService;
    }
}