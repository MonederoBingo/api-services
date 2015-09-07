package com.lealpoints.controller.api;

import com.lealpoints.service.implementations.PromotionServiceImpl;
import com.lealpoints.service.model.PromotionApplying;
import com.lealpoints.service.model.ServiceResult;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PromotionControllerTest {

    @Test
    public void testAwardPoints() throws Exception {
        ServiceResult<Long> expectedServiceResult = new ServiceResult<>(true, "", 1l);
        PromotionServiceImpl promotionService = createPromotionService(expectedServiceResult);
        PromotionController pointsController = new PromotionController(promotionService);
        PromotionApplying pointsAwarding = new PromotionApplying();
        pointsAwarding.setPromotionConfigurationId(1);
        pointsAwarding.setCompanyId(1);
        pointsAwarding.setPhone("1234567890");

        ResponseEntity<ServiceResult<Long>> responseEntity = pointsController.applyPromotion(pointsAwarding);
        assertNotNull(responseEntity);
        ServiceResult<Long> actualServiceResults = responseEntity.getBody();
        assertNotNull(actualServiceResults);
        assertEquals(expectedServiceResult.isSuccess(), actualServiceResults.isSuccess());
        assertEquals(expectedServiceResult.getMessage(), actualServiceResults.getMessage());
        assertEquals(expectedServiceResult.getObject(), actualServiceResults.getObject());

        verify(promotionService);
    }

    private PromotionServiceImpl createPromotionService(ServiceResult<Long> serviceResult) throws Exception {
        PromotionServiceImpl pointsService = createMock(PromotionServiceImpl.class);
        expect(pointsService.applyPromotion((PromotionApplying) anyObject())).andReturn(serviceResult);
        replay(pointsService);
        return pointsService;
    }
}