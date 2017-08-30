package com.monederobingo.controller.api.v1;

import com.lealpoints.model.PointsInCompany;
import com.lealpoints.service.implementations.CompanyServiceImpl;
import org.easymock.EasyMock;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PointsInCompanyControllerTest {
    @Test
    public void testGetByCompanyId() throws Exception {
        //given
        JSONArray expectedPointsInCompanies = new JSONArray();
        expectedPointsInCompanies.put(createCompany(1, "name1", "logo1", 100));
        expectedPointsInCompanies.put(createCompany(2, "name2", "logo2", 200));
        final xyz.greatapp.libs.service.ServiceResult expectedServiceResult =
                new xyz.greatapp.libs.service.ServiceResult(true, "", expectedPointsInCompanies.toString());
        final CompanyServiceImpl clientService = createCompanyServiceForGetPoints(expectedServiceResult);
        final PointsInCompanyController pointsInCompanyController = new PointsInCompanyController(clientService);

        //when
        ResponseEntity<xyz.greatapp.libs.service.ServiceResult> responseEntity = pointsInCompanyController.getByClientId("1234567890");

        //then
        assertNotNull(responseEntity);
        xyz.greatapp.libs.service.ServiceResult actualServiceResults = responseEntity.getBody();
        assertNotNull(actualServiceResults);
        assertEquals(expectedServiceResult.isSuccess(), actualServiceResults.isSuccess());
        assertEquals(expectedServiceResult.getMessage(), actualServiceResults.getMessage());
        assertNotNull(expectedServiceResult.getObject());

        JSONArray actualCompanies = new JSONArray(actualServiceResults.getObject());
        assertEquals(2, actualCompanies.length());

        JSONObject company1 = actualCompanies.getJSONObject(0);
        assertEquals(1, company1.getInt("company_id"));
        assertEquals("name1", company1.getString("name"));
        assertEquals("logo1", company1.getString("url_image_logo"));
        assertEquals(100, company1.getDouble("points"), 0.00);

        JSONObject company2 = actualCompanies.getJSONObject(1);
        assertEquals(2, company2.getInt("company_id"));
        assertEquals("name2", company2.getString("name"));
        assertEquals("logo2", company2.getString("url_image_logo"));
        assertEquals(200, company2.getDouble("points"), 0.00);
        verify(clientService);
    }

    private CompanyServiceImpl createCompanyServiceForGetPoints(xyz.greatapp.libs.service.ServiceResult serviceResult) throws Exception {
        final CompanyServiceImpl companyService = EasyMock.createMock(CompanyServiceImpl.class);
        expect(companyService.getPointsInCompanyByPhone(anyString())).andReturn(serviceResult).times(1);
        replay(companyService);
        return companyService;
    }

    private JSONObject createCompany(long companyId, String name, String urlImageLogo, float points) {
        PointsInCompany pointsInCompany = new PointsInCompany();
        pointsInCompany.setCompanyId(companyId);
        pointsInCompany.setName(name);
        pointsInCompany.setUrlImageLogo(urlImageLogo);
        pointsInCompany.setPoints(points);
        return pointsInCompany.toJSONObject();
    }

}
