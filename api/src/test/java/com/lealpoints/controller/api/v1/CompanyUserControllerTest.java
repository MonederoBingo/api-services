package com.lealpoints.controller.api.v1;

import com.lealpoints.service.model.CompanyUserRegistration;
import com.lealpoints.service.response.ServiceMessage;
import com.lealpoints.service.response.ServiceResult;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.*;

public class CompanyUserControllerTest {
@Test
public void testRegister() throws Exception {
    final ServiceResult expectedServiceResult = new ServiceResult(false, new ServiceMessage("Operation not supported yet"));
    final CompanyUserRegistration companyUserRegistration = new CompanyUserRegistration();
    companyUserRegistration.setName("name");
    companyUserRegistration.setEmail("email@test.com");
    ResponseEntity<ServiceResult> responseEntity = new CompanyUserController().register(companyUserRegistration);
    assertNotNull(responseEntity);
    ServiceResult actualServiceResults = responseEntity.getBody();
    assertNotNull(actualServiceResults);
    assertEquals(expectedServiceResult.isSuccess(), actualServiceResults.isSuccess());
    assertEquals(expectedServiceResult.getMessage(), actualServiceResults.getMessage());
}
}