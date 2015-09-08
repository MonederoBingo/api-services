package com.lealpoints.service.implementations;

import com.lealpoints.model.PointsConfiguration;
import com.lealpoints.repository.PointsConfigurationRepository;
import com.lealpoints.service.model.ServiceResult;
import com.lealpoints.util.Translations;
import org.junit.Test;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class PointsConfigurationServiceImplTest {

    @Test
    public void testGetByCompanyId() throws Exception {
        PointsConfiguration expectedPointsConfiguration = new PointsConfiguration();
        expectedPointsConfiguration.setCompanyId(1);
        expectedPointsConfiguration.setPointsToEarn(10);
        expectedPointsConfiguration.setRequiredAmount(100);
        PointsConfigurationRepository pointsConfigurationRepository = createPointsConfigurationRepositoryForGet(expectedPointsConfiguration);
        PointsConfigurationServiceImpl pointsConfigurationService = new PointsConfigurationServiceImpl(pointsConfigurationRepository, null, null);

        ServiceResult<PointsConfiguration> serviceResult = pointsConfigurationService.getByCompanyId(1);
        assertNotNull(serviceResult);
        assertTrue(serviceResult.isSuccess());
        assertEquals("", serviceResult.getMessage());
        assertNotNull(serviceResult.getObject());
        PointsConfiguration actualPointsConfiguration = serviceResult.getObject();
        assertEquals(expectedPointsConfiguration.getCompanyId(), actualPointsConfiguration.getCompanyId());
        assertEquals(expectedPointsConfiguration.getPointsToEarn(), actualPointsConfiguration.getPointsToEarn(), 0.00);
        assertEquals(expectedPointsConfiguration.getRequiredAmount(), actualPointsConfiguration.getRequiredAmount(), 0.00);

        verify(pointsConfigurationRepository);
    }

    @Test
    public void testUpdate() throws Exception {
        PointsConfigurationRepository pointsConfigurationRepository = createPointsConfigurationRepositoryForUpdate();
        PointsConfigurationServiceImpl pointsConfigurationService = new PointsConfigurationServiceImpl(pointsConfigurationRepository, null, null) {
            @Override
            public String getTranslation(Translations.Message message) {
                return message.name();
            }
        };
        ServiceResult<Boolean> serviceResult = pointsConfigurationService.update(new PointsConfiguration());
        assertNotNull(serviceResult);
        assertTrue(serviceResult.isSuccess());
        assertEquals(Translations.Message.CONFIGURATION_UPDATED.name(), serviceResult.getMessage());
        assertEquals(true, serviceResult.getObject());
        verify(pointsConfigurationRepository);
    }

    private PointsConfigurationRepository createPointsConfigurationRepositoryForUpdate() throws Exception {
        PointsConfigurationRepository pointsConfigurationRepository = createMock(PointsConfigurationRepository.class);
        expect(pointsConfigurationRepository.update((PointsConfiguration) anyObject())).andReturn(1);
        replay(pointsConfigurationRepository);
        return pointsConfigurationRepository;
    }

    private PointsConfigurationRepository createPointsConfigurationRepositoryForGet(PointsConfiguration pointsConfiguration) throws Exception {
        PointsConfigurationRepository pointsConfigurationRepository = createMock(PointsConfigurationRepository.class);
        expect(pointsConfigurationRepository.getByCompanyId(anyLong())).andReturn(pointsConfiguration);
        replay(pointsConfigurationRepository);
        return pointsConfigurationRepository;
    }
}