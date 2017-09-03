package com.monederobingo.controller.api.v1;

import com.lealpoints.model.PointsConfiguration;
import com.lealpoints.service.implementations.PointsConfigurationServiceImpl;
import com.lealpoints.service.response.ServiceMessage;
import com.lealpoints.service.response.ServiceResult;
import org.json.JSONObject;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PointsConfigurationControllerTest {

    @Test
    public void testGet() throws Exception {
        //given
        PointsConfiguration expectedPointsConfiguration = new PointsConfiguration();
        expectedPointsConfiguration.setCompanyId(1);
        expectedPointsConfiguration.setPointsToEarn(10);
        expectedPointsConfiguration.setRequiredAmount(100);
        xyz.greatapp.libs.service.ServiceResult expectedServiceResult =
                new xyz.greatapp.libs.service.ServiceResult(true, "", expectedPointsConfiguration.toJSONObject().toString());
        PointsConfigurationServiceImpl pointsConfigurationService = createPointsConfigurationServiceForGet(expectedServiceResult);
        PointsConfigurationController pointsConfigurationController = new PointsConfigurationController(pointsConfigurationService);

        //when
        ResponseEntity<xyz.greatapp.libs.service.ServiceResult> responseEntity = pointsConfigurationController.get(1);

        //then
        assertNotNull(responseEntity);
        xyz.greatapp.libs.service.ServiceResult actualServiceResults = responseEntity.getBody();
        assertNotNull(actualServiceResults);
        assertEquals(expectedServiceResult.isSuccess(), actualServiceResults.isSuccess());
        assertEquals(expectedServiceResult.getMessage(), actualServiceResults.getMessage());
        assertNotNull(actualServiceResults.getObject());
        JSONObject jsonObject = new JSONObject(actualServiceResults.getObject());
        assertNotNull(jsonObject);
        assertEquals(expectedPointsConfiguration.getCompanyId(), jsonObject.getLong("company_id"));
        assertEquals(expectedPointsConfiguration.getPointsToEarn(), jsonObject.getDouble("points_to_earn"), 0.00);
        assertEquals(expectedPointsConfiguration.getRequiredAmount(), jsonObject.getDouble("required_amount"), 0.00);

        verify(pointsConfigurationService);
    }

    @Test
    public void testUpdate() throws Exception {
        ServiceResult<Boolean> expectedServiceResult = new ServiceResult<>(true, ServiceMessage.EMPTY, true);
        PointsConfigurationServiceImpl pointsConfigurationService = createPointsConfigurationServiceForUpdate(expectedServiceResult);
        PointsConfigurationController pointsConfigurationController = new PointsConfigurationController(pointsConfigurationService);

        ResponseEntity<ServiceResult<Boolean>> responseEntity = pointsConfigurationController.update(new PointsConfiguration());
        assertNotNull(responseEntity);
        ServiceResult actualServiceResults = responseEntity.getBody();
        assertNotNull(actualServiceResults);
        assertEquals(expectedServiceResult.isSuccess(), actualServiceResults.isSuccess());
        assertEquals(expectedServiceResult.getMessage(), actualServiceResults.getMessage());
        assertEquals(expectedServiceResult.getObject(), actualServiceResults.getObject());

        verify(pointsConfigurationService);
    }

    private PointsConfigurationServiceImpl createPointsConfigurationServiceForGet(xyz.greatapp.libs.service.ServiceResult serviceResult) throws Exception {
        PointsConfigurationServiceImpl pointsConfigurationService = createMock(PointsConfigurationServiceImpl.class);
        expect(pointsConfigurationService.getByCompanyId(anyLong()))
                .andReturn(serviceResult);
        replay(pointsConfigurationService);
        return pointsConfigurationService;
    }

    private PointsConfigurationServiceImpl createPointsConfigurationServiceForUpdate(ServiceResult<Boolean> serviceResult) throws Exception {
        PointsConfigurationServiceImpl pointsConfigurationService = createMock(PointsConfigurationServiceImpl.class);
        expect(pointsConfigurationService.update(anyObject())).andReturn(serviceResult);
        replay(pointsConfigurationService);
        return pointsConfigurationService;
    }
}
