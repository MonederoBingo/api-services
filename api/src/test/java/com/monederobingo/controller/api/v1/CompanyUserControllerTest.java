package com.monederobingo.controller.api.v1;

import com.lealpoints.model.CompanyUser;
import com.lealpoints.service.implementations.CompanyUserServiceImpl;
import com.lealpoints.service.model.CompanyUserRegistration;
import com.lealpoints.service.response.ServiceMessage;
import com.lealpoints.service.response.ServiceResult;
import org.easymock.EasyMock;
import org.json.JSONArray;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

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
        //given
        JSONArray expectedUsers = new JSONArray();
        expectedUsers.put(createCompanyUser("fernando", "fernando@monederobingo.com").toJSONObject());
        expectedUsers.put(createCompanyUser("alonso", "alonso@monederobingo.com").toJSONObject());
        final xyz.greatapp.libs.service.ServiceResult expectedServiceResult =
                new xyz.greatapp.libs.service.ServiceResult(true, "", expectedUsers.toString());
        final CompanyUserServiceImpl companyUserService = createCompanyUserServiceForGet(expectedServiceResult);
        final CompanyUserController companyUserController = new CompanyUserController(companyUserService);

        //when
        ResponseEntity<xyz.greatapp.libs.service.ServiceResult> responseEntity = companyUserController.get(1L);

        //then
        assertNotNull(responseEntity);
        xyz.greatapp.libs.service.ServiceResult actualServiceResults = responseEntity.getBody();
        assertNotNull(actualServiceResults);
        assertEquals(expectedServiceResult.isSuccess(), actualServiceResults.isSuccess());
        assertEquals(expectedServiceResult.getMessage(), actualServiceResults.getMessage());
        assertNotNull(expectedServiceResult.getObject());

        JSONArray companyUser = new JSONArray(actualServiceResults.getObject());
        assertEquals(2, companyUser.length());

        assertEquals("fernando", companyUser.getJSONObject(0).getString("name"));
        assertEquals("fernando@monederobingo.com", companyUser.getJSONObject(0).getString("email"));
        assertEquals("alonso", companyUser.getJSONObject(1).getString("name"));
        assertEquals("alonso@monederobingo.com", companyUser.getJSONObject(1).getString("email"));
    }

    private CompanyUserServiceImpl createCompanyUserServiceForGet(xyz.greatapp.libs.service.ServiceResult serviceResult) throws Exception {
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
        expect(companyUserService.register(anyObject())).andReturn(serviceResult).times(1);
        replay(companyUserService);
        return companyUserService;
    }
}
