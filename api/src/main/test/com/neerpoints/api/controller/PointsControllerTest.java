package com.neerpoints.api.controller;

import com.neerpoints.service.PointsService;
import com.neerpoints.service.model.PointsAwarding;
import com.neerpoints.service.model.ServiceResult;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PointsControllerTest {

    @Test
    public void testAwardPoints() throws Exception {
        ServiceResult<Float> expectedServiceResult = new ServiceResult<>(true, "", 10.0f);
        PointsService pointsService = createPointsService(expectedServiceResult);
        PointsController pointsController = new PointsController(pointsService);
        PointsAwarding pointsAwarding = new PointsAwarding();
        pointsAwarding.setSaleKey("A1234");
        pointsAwarding.setPhone("1234567890");
        pointsAwarding.setSaleAmount(100);

        ResponseEntity<ServiceResult<Float>> responseEntity = pointsController.awardPoints(pointsAwarding);
        assertNotNull(responseEntity);
        ServiceResult<Float> actualServiceResults = responseEntity.getBody();
        assertNotNull(actualServiceResults);
        assertEquals(expectedServiceResult.isSuccess(), actualServiceResults.isSuccess());
        assertEquals(expectedServiceResult.getMessage(), actualServiceResults.getMessage());
        assertEquals(expectedServiceResult.getObject(), actualServiceResults.getObject());

        verify(pointsService);
    }

    private PointsService createPointsService(ServiceResult<Float> serviceResult) throws Exception {
        PointsService pointsService = createMock(PointsService.class);
        expect(pointsService.awardPoints((PointsAwarding) anyObject())).andReturn(serviceResult);
        replay(pointsService);
        return pointsService;
    }
}