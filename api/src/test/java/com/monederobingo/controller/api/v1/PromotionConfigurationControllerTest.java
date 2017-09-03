package com.monederobingo.controller.api.v1;

import com.lealpoints.model.PromotionConfiguration;
import com.lealpoints.service.implementations.PromotionConfigurationServiceImpl;
import com.lealpoints.service.response.ServiceMessage;
import com.lealpoints.service.response.ServiceResult;
import org.json.JSONArray;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class PromotionConfigurationControllerTest {

    @Test
    public void testGet() throws Exception {
        //given
        JSONArray expectedPromotionConfigurations = new JSONArray();
        expectedPromotionConfigurations.put(
                createPromotionConfiguration(1, 1, "10% off", 1200).toJSONObject());
        expectedPromotionConfigurations.put(
                createPromotionConfiguration(2, 1, "20% off", 2400).toJSONObject());
        xyz.greatapp.libs.service.ServiceResult expectedServiceResult =
                new xyz.greatapp.libs.service.ServiceResult(true, "", expectedPromotionConfigurations.toString());
        PromotionConfigurationServiceImpl pointsConfigurationService = createPromotionConfigurationServiceForGet(expectedServiceResult);
        PromotionConfigurationController pointsConfigurationController = new PromotionConfigurationController(pointsConfigurationService);

        //when
        ResponseEntity<xyz.greatapp.libs.service.ServiceResult> responseEntity = pointsConfigurationController.get(1);

        //then
        assertNotNull(responseEntity);
        xyz.greatapp.libs.service.ServiceResult serviceResult = responseEntity.getBody();
        assertNotNull(serviceResult);
        assertEquals(expectedServiceResult.isSuccess(), serviceResult.isSuccess());
        assertEquals(expectedServiceResult.getMessage(), serviceResult.getMessage());
        JSONArray actualPromotionConfigurations = new JSONArray(serviceResult.getObject());
        assertNotNull(actualPromotionConfigurations);
        assertEquals(2, actualPromotionConfigurations.length());
        assertNotNull(actualPromotionConfigurations.get(0));
        assertEquals(1, actualPromotionConfigurations.getJSONObject(0).getLong("promotion_configuration_id"));
        assertEquals(1, actualPromotionConfigurations.getJSONObject(0).getLong("company_id"));
        assertEquals("10% off", actualPromotionConfigurations.getJSONObject(0).getString("description"));
        assertEquals(1200, actualPromotionConfigurations.getJSONObject(0).getDouble("required_points"), 0.00);
        assertNotNull(actualPromotionConfigurations.get(1));
        assertEquals(2, actualPromotionConfigurations.getJSONObject(1).getLong("promotion_configuration_id"));
        assertEquals(1, actualPromotionConfigurations.getJSONObject(1).getLong("company_id"));
        assertEquals("20% off", actualPromotionConfigurations.getJSONObject(1).getString("description"));
        assertEquals(2400, actualPromotionConfigurations.getJSONObject(1).getDouble("required_points"), 0.00);

        verify(pointsConfigurationService);
    }

    @Test
    public void testInsert() throws Exception {

        ServiceResult<Long> expectedServiceResult = new ServiceResult<>(true, new ServiceMessage("Promotion updated"), 1L);
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
        //given
        JSONArray expectedPromotionConfigurations = new JSONArray();
        expectedPromotionConfigurations.put(
                createPromotionConfiguration(1, 1, "5% off", 600).toJSONObject());
        expectedPromotionConfigurations.put(
                createPromotionConfiguration(2, 1, "10% off", 1000).toJSONObject());
        xyz.greatapp.libs.service.ServiceResult expectedServiceResult =
                new xyz.greatapp.libs.service.ServiceResult(true, "", expectedPromotionConfigurations.toString());
        PromotionConfigurationServiceImpl pointsConfigurationService =
                createPromotionConfigurationServiceForGetByRequiredPoints(expectedServiceResult);
        PromotionConfigurationController pointsConfigurationController = new PromotionConfigurationController(pointsConfigurationService);

        //when
        ResponseEntity<xyz.greatapp.libs.service.ServiceResult> responseEntity =
                pointsConfigurationController.getAvailableByPhone(1, "1234567890");

        //then
        assertNotNull(responseEntity);
        xyz.greatapp.libs.service.ServiceResult serviceResult = responseEntity.getBody();
        assertNotNull(serviceResult);
        assertEquals(expectedServiceResult.isSuccess(), serviceResult.isSuccess());
        assertEquals(expectedServiceResult.getMessage(), serviceResult.getMessage());
        JSONArray actualPromotionConfigurations = new JSONArray(serviceResult.getObject());
        assertNotNull(actualPromotionConfigurations);
        assertEquals(2, actualPromotionConfigurations.length());
        assertNotNull(actualPromotionConfigurations.get(0));
        assertEquals(1, actualPromotionConfigurations.getJSONObject(0).getLong("promotion_configuration_id"));
        assertEquals(1, actualPromotionConfigurations.getJSONObject(0).getLong("company_id"));
        assertEquals("5% off", actualPromotionConfigurations.getJSONObject(0).getString("description"));
        assertEquals(600, actualPromotionConfigurations.getJSONObject(0).getDouble("required_points"), 0.00);
        assertNotNull(actualPromotionConfigurations.get(1));
        assertEquals(2, actualPromotionConfigurations.getJSONObject(1).getLong("promotion_configuration_id"));
        assertEquals(1, actualPromotionConfigurations.getJSONObject(1).getLong("company_id"));
        assertEquals("10% off", actualPromotionConfigurations.getJSONObject(1).getString("description"));
        assertEquals(1000, actualPromotionConfigurations.getJSONObject(1).getDouble("required_points"), 0.00);

        verify(pointsConfigurationService);
    }

    @Test
    public void testDelete() throws Exception {
        PromotionConfigurationServiceImpl promotionConfigurationService =
                createPromotionConfigurationServiceForDelete(new ServiceResult<Boolean>(true, ServiceMessage.EMPTY));
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

    private PromotionConfigurationServiceImpl createPromotionConfigurationServiceForGet(xyz.greatapp.libs.service.ServiceResult serviceResult)
            throws Exception {
        PromotionConfigurationServiceImpl pointsConfigurationService = createMock(PromotionConfigurationServiceImpl.class);
        expect(pointsConfigurationService.getByCompanyId(anyLong()))
                .andReturn(serviceResult);
        replay(pointsConfigurationService);
        return pointsConfigurationService;
    }

    private PromotionConfigurationServiceImpl createPromotionConfigurationServiceForGetByRequiredPoints(
            xyz.greatapp.libs.service.ServiceResult serviceResult) throws Exception {
        PromotionConfigurationServiceImpl pointsConfigurationService = createMock(PromotionConfigurationServiceImpl.class);
        expect(pointsConfigurationService.getByCompanyIdRequiredPoints(anyLong(), anyString()))
                .andReturn(serviceResult);
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
