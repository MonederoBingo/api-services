package com.lealpoints.controller.api;

import java.util.ArrayList;
import java.util.List;
import com.lealpoints.model.PointsInCompany;
import com.lealpoints.service.implementations.CompanyServiceImpl;
import com.lealpoints.service.model.ServiceResult;
import org.easymock.EasyMock;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PointsInCompanyControllerTest {
    @Test
    public void testGetByCompanyId() throws Exception {
        List<PointsInCompany> expectedPointsInCompanies = new ArrayList<>();
        expectedPointsInCompanies.add(createCompany(1, "name1", "logo1", 100));
        expectedPointsInCompanies.add(createCompany(2, "name2", "logo2", 200));
        final ServiceResult<List<PointsInCompany>> expectedServiceResult = new ServiceResult<>(true, "1", expectedPointsInCompanies);
        final CompanyServiceImpl clientService = createCompanyServiceForGetPoints(expectedServiceResult);
        final PointsInCompanyController pointsInCompanyController = new PointsInCompanyController(clientService);

        ResponseEntity<ServiceResult<List<PointsInCompany>>> responseEntity = pointsInCompanyController.getByClientId("1234567890");
        assertNotNull(responseEntity);
        ServiceResult<List<PointsInCompany>> actualServiceResults = responseEntity.getBody();
        assertNotNull(actualServiceResults);
        assertEquals(expectedServiceResult.isSuccess(), actualServiceResults.isSuccess());
        assertEquals(expectedServiceResult.getMessage(), actualServiceResults.getMessage());
        assertNotNull(expectedServiceResult.getObject());
        List<PointsInCompany> actualCompanies = actualServiceResults.getObject();
        assertEquals(2, actualCompanies.size());
        assertEquals(1, actualCompanies.get(0).getCompanyId());
        assertEquals("name1", actualCompanies.get(0).getName());
        assertEquals("logo1", actualCompanies.get(0).getUrlImageLogo());
        assertEquals(100, actualCompanies.get(0).getPoints(), 0.00);
        assertEquals(2, actualCompanies.get(1).getCompanyId());
        assertEquals("name2", actualCompanies.get(1).getName());
        assertEquals("logo2", actualCompanies.get(1).getUrlImageLogo());
        assertEquals(200, actualCompanies.get(1).getPoints(), 0.00);
        verify(clientService);
    }

    private CompanyServiceImpl createCompanyServiceForGetPoints(ServiceResult<List<PointsInCompany>> serviceResult) throws Exception {
        final CompanyServiceImpl companyService = EasyMock.createMock(CompanyServiceImpl.class);
        expect(companyService.getPointsInCompanyByPhone(anyString())).andReturn(serviceResult).times(1);
        replay(companyService);
        return companyService;
    }

    private PointsInCompany createCompany(long companyId, String name, String urlImageLogo, float points) {
        PointsInCompany pointsInCompany = new PointsInCompany();
        pointsInCompany.setCompanyId(companyId);
        pointsInCompany.setName(name);
        pointsInCompany.setUrlImageLogo(urlImageLogo);
        pointsInCompany.setPoints(points);
        return pointsInCompany;
    }

}