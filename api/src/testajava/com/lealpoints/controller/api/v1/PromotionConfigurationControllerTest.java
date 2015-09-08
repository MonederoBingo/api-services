package com.lealpoints.controller.api.v1;

import java.util.ArrayList;
import java.util.List;
import com.lealpoints.model.PromotionConfiguration;
import com.lealpoints.service.implementations.PromotionConfigurationServiceImpl;
import com.lealpoints.service.model.ServiceResult;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class PromotionConfigurationControllerTest {

    @Test
    public void testGet() throws Exception {
        List<PromotionConfiguration> expectedPromotionConfigurations = new ArrayList<>();
        expectedPromotionConfigurations.add(createPromotionConfiguration(1, 1, "10% off", 1200));
        expectedPromotionConfigurations.add(createPromotionConfiguration(2, 1, "20% off", 2400));
        ServiceResult<List<PromotionConfiguration>> expectedServiceResult = new ServiceResult<>(true, "", expectedPromotionConfigurations);
        PromotionConfigurationServiceImpl pointsConfigurationService = createPromotionConfigurationServiceForGet(expectedServiceResult);
        PromotionConfigurationController pointsConfigurationController = new PromotionConfigurationController(pointsConfigurationService);

        ResponseEntity<ServiceResult<List<PromotionConfiguration>>> responseEntity = pointsConfigurationController.get(1);
        assertNotNull(responseEntity);
        ServiceResult<List<PromotionConfiguration>> serviceResult = responseEntity.getBody();
        assertNotNull(serviceResult);
        assertEquals(expectedServiceResult.isSuccess(), serviceResult.isSuccess());
        assertEquals(expectedServiceResult.getMessage(), serviceResult.getMessage());
        List<PromotionConfiguration> actualPromotionConfigurations = serviceResult.getObject();
        assertNotNull(actualPromotionConfigurations);
        assertEquals(2, actualPromotionConfigurations.size());
        assertNotNull(actualPromotionConfigurations.get(0));
        assertEquals(1, actualPromotionConfigurations.get(0).getPromotionConfigurationId());
        assertEquals(1, actualPromotionConfigurations.get(0).getCompanyId());
        assertEquals("10% off", actualPromotionConfigurations.get(0).getDescription());
        assertEquals(1200, actualPromotionConfigurations.get(0).getRequiredPoints(), 0.00);
        assertNotNull(actualPromotionConfigurations.get(1));
        assertEquals(2, actualPromotionConfigurations.get(1).getPromotionConfigurationId());
        assertEquals(1, actualPromotionConfigurations.get(1).getCompanyId());
        assertEquals("20% off", actualPromotionConfigurations.get(1).getDescription());
        assertEquals(2400, actualPromotionConfigurations.get(1).getRequiredPoints(), 0.00);

        verify(pointsConfigurationService);
    }

    @Test
    public void testInsert() throws Exception {

        ServiceResult<Long> expectedServiceResult = new ServiceResult<>(true, "Promotion updated", 1l);
        PromotionConfigurationServiceImpl promotionConfigurationService = createPromotionConfigurationService(expectedServiceResult);
        PromotionConfigurationController promotionConfigurationController = new PromotionConfigurationController(promotionConfigurationService);

        ResponseEntity<ServiceResult<Long>> responseEntity = promotionConfigurationController.insert(new PromotionConfiguration());
        assertNotNull(responseEntity);
        ServiceResult actualServiceResults = responseEntity.getBody();
        assertNotNull(actualServiceResults);
        assertEquals(expectedServiceResult.isSuccess(), actualServiceResults.isSuccess());
        assertEquals(expectedServiceResult.getMessage(), actualServiceResults.getMessage());
        assertEquals(expectedServiceResult.getObject(), actualServiceResults.getObject());

        verify(promotionConfigurationService);
    }

    @Test
    public void testGetByPhone() throws Exception {
        List<PromotionConfiguration> expectedPromotionConfigurations = new ArrayList<>();
        expectedPromotionConfigurations.add(createPromotionConfiguration(1, 1, "5% off", 600));
        expectedPromotionConfigurations.add(createPromotionConfiguration(2, 1, "10% off", 1000));
        ServiceResult<List<PromotionConfiguration>> expectedServiceResult = new ServiceResult<>(true, "", expectedPromotionConfigurations);
        PromotionConfigurationServiceImpl pointsConfigurationService =
            createPromotionConfigurationServiceForGetByRequiredPoints(expectedServiceResult);
        PromotionConfigurationController pointsConfigurationController = new PromotionConfigurationController(pointsConfigurationService);

        ResponseEntity<ServiceResult<List<PromotionConfiguration>>> responseEntity =
            pointsConfigurationController.getAvailableByPhone(1, "1234567890");
        assertNotNull(responseEntity);
        ServiceResult<List<PromotionConfiguration>> serviceResult = responseEntity.getBody();
        assertNotNull(serviceResult);
        assertEquals(expectedServiceResult.isSuccess(), serviceResult.isSuccess());
        assertEquals(expectedServiceResult.getMessage(), serviceResult.getMessage());
        List<PromotionConfiguration> actualPromotionConfigurations = serviceResult.getObject();
        assertNotNull(actualPromotionConfigurations);
        assertEquals(2, actualPromotionConfigurations.size());
        assertNotNull(actualPromotionConfigurations.get(0));
        assertEquals(1, actualPromotionConfigurations.get(0).getPromotionConfigurationId());
        assertEquals(1, actualPromotionConfigurations.get(0).getCompanyId());
        assertEquals("5% off", actualPromotionConfigurations.get(0).getDescription());
        assertEquals(600, actualPromotionConfigurations.get(0).getRequiredPoints(), 0.00);
        assertNotNull(actualPromotionConfigurations.get(1));
        assertEquals(2, actualPromotionConfigurations.get(1).getPromotionConfigurationId());
        assertEquals(1, actualPromotionConfigurations.get(1).getCompanyId());
        assertEquals("10% off", actualPromotionConfigurations.get(1).getDescription());
        assertEquals(1000, actualPromotionConfigurations.get(1).getRequiredPoints(), 0.00);

        verify(pointsConfigurationService);
    }

    @Test
    public void testDelete() throws Exception {
        PromotionConfigurationServiceImpl promotionConfigurationService =
            createPromotionConfigurationServiceForDelete(new ServiceResult<Boolean>(true, ""));
        PromotionConfigurationController promotionConfigurationController = new PromotionConfigurationController(promotionConfigurationService);
        final ResponseEntity<ServiceResult<Boolean>> responseEntity = promotionConfigurationController.delete(1);
        assertNotNull(responseEntity);
        ServiceResult<Boolean> serviceResult = responseEntity.getBody();
        assertNotNull(serviceResult);
        assertTrue(serviceResult.isSuccess());
    }

    private PromotionConfigurationServiceImpl createPromotionConfigurationService(ServiceResult<Long> serviceResult) throws Exception {
        PromotionConfigurationServiceImpl promotionConfigurationService = createMock(PromotionConfigurationServiceImpl.class);
        expect(promotionConfigurationService.insert((PromotionConfiguration) anyObject())).andReturn(serviceResult);
        replay(promotionConfigurationService);
        return promotionConfigurationService;
    }

    private PromotionConfiguration createPromotionConfiguration(long promotionConfigurationId, long companyId, String description,
        float requiredPoints) {
        PromotionConfiguration promotionConfiguration = new PromotionConfiguration();
        promotionConfiguration.setPromotionConfigurationId(promotionConfigurationId);
        promotionConfiguration.setCompanyId(companyId);
        promotionConfiguration.setDescription(description);
        promotionConfiguration.setRequiredPoints(requiredPoints);
        return promotionConfiguration;
    }

    private PromotionConfigurationServiceImpl createPromotionConfigurationServiceForGet(ServiceResult<List<PromotionConfiguration>> serviceResult)
        throws Exception {
        PromotionConfigurationServiceImpl pointsConfigurationService = createMock(PromotionConfigurationServiceImpl.class);
        expect(pointsConfigurationService.getByCompanyId(anyLong())).andReturn(serviceResult);
        replay(pointsConfigurationService);
        return pointsConfigurationService;
    }

    private PromotionConfigurationServiceImpl createPromotionConfigurationServiceForGetByRequiredPoints(
        ServiceResult<List<PromotionConfiguration>> serviceResult) throws Exception {
        PromotionConfigurationServiceImpl pointsConfigurationService = createMock(PromotionConfigurationServiceImpl.class);
        expect(pointsConfigurationService.getByCompanyIdRequiredPoints(anyLong(), anyString())).andReturn(serviceResult);
        replay(pointsConfigurationService);
        return pointsConfigurationService;
    }

    private PromotionConfigurationServiceImpl createPromotionConfigurationServiceForDelete(ServiceResult<Boolean> serviceResult) throws Exception {
        PromotionConfigurationServiceImpl pointsConfigurationService = createMock(PromotionConfigurationServiceImpl.class);
        expect(pointsConfigurationService.deletePromotionConfiguration(anyLong())).andReturn(serviceResult);
        replay(pointsConfigurationService);
        return pointsConfigurationService;
    }
}