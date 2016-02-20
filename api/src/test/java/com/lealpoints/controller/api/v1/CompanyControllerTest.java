package com.lealpoints.controller.api.v1;

import com.lealpoints.i18n.Message;
import com.lealpoints.model.Company;
import com.lealpoints.service.implementations.CompanyServiceImpl;
import com.lealpoints.service.response.ServiceMessage;
import com.lealpoints.service.response.ServiceResult;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.easymock.EasyMock;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class CompanyControllerTest {

    @Test
    public void testUpdateLogo() throws FileUploadException {
        CompanyServiceImpl companyService = createCompanyService(new ServiceResult<Boolean>(true,
                new ServiceMessage(Message.YOUR_LOGO_WAS_UPDATED.name())));
        final ServletFileUpload servletFileUpload = createMock(ServletFileUpload.class);
        expect(servletFileUpload.parseRequest((HttpServletRequest) anyObject())).andReturn(new ArrayList<FileItem>());
        replay(servletFileUpload);
        CompanyController companyController = new CompanyController(companyService) {
            @Override
            ServletFileUpload getServletFileUpload() {
                return servletFileUpload;
            }
        };

        ResponseEntity<ServiceResult> responseEntity = companyController.updateLogo(1, createMock(HttpServletRequest.class));
        assertNotNull(responseEntity);
        ServiceResult serviceResult = responseEntity.getBody();
        assertNotNull(serviceResult);
        assertTrue(serviceResult.isSuccess());
        assertEquals(Message.YOUR_LOGO_WAS_UPDATED.name(), serviceResult.getMessage());
        verify(companyService);
    }

    @Test
    public void testGet() throws Exception {
        Company company = new Company();
        company.setName("name");
        company.setUrlImageLogo("logo.png");
        ServiceResult<Company> serviceResult = new ServiceResult<>(true, ServiceMessage.EMPTY, company);
        CompanyServiceImpl companyService = createCompanyServiceForGet(serviceResult);
        CompanyController companyController = new CompanyController(companyService);
        ResponseEntity<ServiceResult<Company>> responseEntity = companyController.get(1);
        assertNotNull(responseEntity);
        ServiceResult actualServiceResult = responseEntity.getBody();
        assertNotNull(actualServiceResult);
        assertTrue(actualServiceResult.isSuccess());
        assertNotNull(actualServiceResult.getObject());
        Company actualCompany = serviceResult.getObject();
        assertEquals("name", actualCompany.getName());
        assertEquals("logo.png", actualCompany.getUrlImageLogo());
        verify(companyService);
    }

    private CompanyServiceImpl createCompanyService(ServiceResult<Boolean> serviceResult) {
        CompanyServiceImpl companyService = createMock(CompanyServiceImpl.class);
        expect(companyService.updateLogo((List<FileItem>) anyObject(), anyLong())).andReturn(serviceResult);
        replay(companyService);
        return companyService;
    }

    private CompanyServiceImpl createCompanyServiceForGet(ServiceResult<Company> company) throws Exception {
        final CompanyServiceImpl companyService = EasyMock.createMock(CompanyServiceImpl.class);
        expect(companyService.getByCompanyId(anyLong())).andReturn(company).times(1);
        replay(companyService);
        return companyService;
    }
}