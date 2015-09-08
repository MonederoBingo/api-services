package com.lealpoints.controller.api.v1;

import com.lealpoints.model.PointsConfiguration;
import com.lealpoints.service.implementations.PointsConfigurationServiceImpl;
import com.lealpoints.service.model.ServiceResult;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PointsConfigurationControllerTest {

    @Test
    public void testGet() throws Exception {
        PointsConfiguration expectedPointsConfiguration = new PointsConfiguration();
        expectedPointsConfiguration.setCompanyId(1);
        expectedPointsConfiguration.setPointsToEarn(10);
        expectedPointsConfiguration.setRequiredAmount(100);
        ServiceResult<PointsConfiguration> expectedServiceResult = new ServiceResult<>(true, "", expectedPointsConfiguration);
        PointsConfigurationServiceImpl pointsConfigurationService = createPointsConfigurationServiceForGet(expectedServiceResult);
        PointsConfigurationController pointsConfigurationController = new PointsConfigurationController(pointsConfigurationService);

        ResponseEntity<ServiceResult<PointsConfiguration>> responseEntity = pointsConfigurationController.get(1);
        assertNotNull(responseEntity);
        ServiceResult<PointsConfiguration> actualServiceResults = responseEntity.getBody();
        assertNotNull(actualServiceResults);
        assertEquals(expectedServiceResult.isSuccess(), actualServiceResults.isSuccess());
        assertEquals(expectedServiceResult.getMessage(), actualServiceResults.getMessage());
        assertNotNull(actualServiceResults.getObject());
        PointsConfiguration actualPointsConfiguration = actualServiceResults.getObject();
        assertNotNull(actualPointsConfiguration);
        assertEquals(expectedPointsConfiguration.getCompanyId(), actualPointsConfiguration.getCompanyId());
        assertEquals(expectedPointsConfiguration.getPointsToEarn(), actualPointsConfiguration.getPointsToEarn(), 0.00);
        assertEquals(expectedPointsConfiguration.getRequiredAmount(), actualPointsConfiguration.getRequiredAmount(), 0.00);

        verify(pointsConfigurationService);
    }

    @Test
    public void testUpdate() throws Exception {
        ServiceResult<Boolean> expectedServiceResult = new ServiceResult<>(true, "", true);
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

    private PointsConfigurationServiceImpl createPointsConfigurationServiceForGet(ServiceResult<PointsConfiguration> serviceResult) throws Exception {
        PointsConfigurationServiceImpl pointsConfigurationService = createMock(PointsConfigurationServiceImpl.class);
        expect(pointsConfigurationService.getByCompanyId(anyLong())).andReturn(serviceResult);
        replay(pointsConfigurationService);
        return pointsConfigurationService;
    }

    private PointsConfigurationServiceImpl createPointsConfigurationServiceForUpdate(ServiceResult<Boolean> serviceResult) throws Exception {
        PointsConfigurationServiceImpl pointsConfigurationService = createMock(PointsConfigurationServiceImpl.class);
        expect(pointsConfigurationService.update((PointsConfiguration) anyObject())).andReturn(serviceResult);
        replay(pointsConfigurationService);
        return pointsConfigurationService;
    }
}