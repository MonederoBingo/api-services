package com.neerpoints.service;

import java.util.ArrayList;
import java.util.List;
import com.neerpoints.model.Client;
import com.neerpoints.model.CompanyClientMapping;
import com.neerpoints.model.PromotionConfiguration;
import com.neerpoints.repository.ClientRepository;
import com.neerpoints.repository.CompanyClientMappingRepository;
import com.neerpoints.repository.PromotionConfigurationRepository;
import com.neerpoints.service.model.ServiceResult;
import com.neerpoints.util.Translations;
import org.junit.Test;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class PromotionConfigurationServiceTest {

    @Test
    public void testGetByCompanyId() throws Exception {

        List<PromotionConfiguration> expectedPromotionConfigurations = new ArrayList<>();
        expectedPromotionConfigurations.add(createPromotionConfiguration(1, 1, "10% off", 1200));
        expectedPromotionConfigurations.add(createPromotionConfiguration(2, 1, "20% off", 2400));
        PromotionConfigurationRepository promotionConfigurationRepository =
            createPromotionConfigurationRepositoryForGet(expectedPromotionConfigurations);
        PromotionConfigurationService promotionConfigurationService =
            new PromotionConfigurationService(promotionConfigurationRepository, null, null, null, null);

        ServiceResult<List<PromotionConfiguration>> serviceResult = promotionConfigurationService.getByCompanyId(1);
        assertNotNull(serviceResult);
        assertTrue(serviceResult.isSuccess());
        assertEquals("", serviceResult.getMessage());
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

        verify(promotionConfigurationRepository);
    }

    @Test
    public void testInsert() throws Exception {
        PromotionConfigurationRepository promotionConfigurationRepository = createPromotionConfigurationRepositoryForInsert();
        PromotionConfigurationService promotionConfigurationService =
            new PromotionConfigurationService(promotionConfigurationRepository, null, null, null, null) {
                @Override
                String getTranslation(Translations.Message message) {
                    return message.name();
                }
            };
        PromotionConfiguration promotionConfiguration = new PromotionConfiguration();
        promotionConfiguration.setCompanyId(1);
        promotionConfiguration.setDescription("10% off next purchase");
        promotionConfiguration.setRequiredPoints(1200);
        ServiceResult<Long> serviceResult = promotionConfigurationService.insert(promotionConfiguration);
        assertNotNull(serviceResult);
        assertTrue(serviceResult.isSuccess());
        assertEquals(Translations.Message.PROMOTION_SUCCESSFULLY_ADDED.name(), serviceResult.getMessage());
        assertTrue(serviceResult.getObject() > 0);
    }

    @Test
    public void testGetByCompanyIdRequiredPoints() throws Exception {
        List<PromotionConfiguration> expectedPromotionConfigurations = new ArrayList<>();
        expectedPromotionConfigurations.add(createPromotionConfiguration(1, 1, "5% off", 500));
        expectedPromotionConfigurations.add(createPromotionConfiguration(2, 1, "10% off", 1200));
        expectedPromotionConfigurations.add(createPromotionConfiguration(3, 1, "20% off", 2400));

        PromotionConfigurationRepository promotionConfigurationRepository =
            createPromotionConfigurationRepositoryForGet(expectedPromotionConfigurations);
        CompanyClientMapping companyClientMapping = new CompanyClientMapping();
        companyClientMapping.setPoints(1200);
        ClientRepository clientRepository = createClientRepository(new Client());
        CompanyClientMappingRepository companyClientMappingRepository = createCompanyClientMappingRepository(companyClientMapping);
        PromotionConfigurationService promotionConfigurationService =
            new PromotionConfigurationService(promotionConfigurationRepository, companyClientMappingRepository, clientRepository, null, null);

        ServiceResult<List<PromotionConfiguration>> serviceResult = promotionConfigurationService.getByCompanyIdRequiredPoints(1, "12345");
        assertNotNull(serviceResult);
        assertTrue(serviceResult.isSuccess());
        assertEquals("", serviceResult.getMessage());
        List<PromotionConfiguration> actualPromotionConfigurations = serviceResult.getObject();
        assertNotNull(actualPromotionConfigurations);
        assertEquals(2, actualPromotionConfigurations.size());
        assertNotNull(actualPromotionConfigurations.get(0));
        assertEquals(1, actualPromotionConfigurations.get(0).getPromotionConfigurationId());
        assertEquals(1, actualPromotionConfigurations.get(0).getCompanyId());
        assertEquals("5% off", actualPromotionConfigurations.get(0).getDescription());
        assertEquals(500, actualPromotionConfigurations.get(0).getRequiredPoints(), 0.00);
        assertNotNull(actualPromotionConfigurations.get(1));
        assertEquals(2, actualPromotionConfigurations.get(1).getPromotionConfigurationId());
        assertEquals(1, actualPromotionConfigurations.get(1).getCompanyId());
        assertEquals("10% off", actualPromotionConfigurations.get(1).getDescription());
        assertEquals(1200, actualPromotionConfigurations.get(1).getRequiredPoints(), 0.00);

        verify(promotionConfigurationRepository, companyClientMappingRepository, clientRepository);
    }

    @Test
    public void testGetByCompanyIdRequiredPointsWhenClientDoesNotExist() throws Exception {
        ClientRepository clientRepository = createClientRepository(null);
        PromotionConfigurationService promotionConfigurationService = new PromotionConfigurationService(null, null, clientRepository, null, null) {
            @Override
            String getTranslation(Translations.Message message) {
                return message.name();
            }
        };

        ServiceResult<List<PromotionConfiguration>> serviceResult = promotionConfigurationService.getByCompanyIdRequiredPoints(1, "12345");
        assertNotNull(serviceResult);
        assertFalse(serviceResult.isSuccess());
        assertEquals(Translations.Message.PHONE_NUMBER_DOES_NOT_EXIST.name(), serviceResult.getMessage());

        verify(clientRepository);
    }

    @Test
    public void testGetByCompanyIdRequiredPointsWhenMappingDoesNotExist() throws Exception {
        ClientRepository clientRepository = createClientRepository(new Client());
        CompanyClientMappingRepository companyClientMappingRepository = createCompanyClientMappingRepository(null);
        PromotionConfigurationService promotionConfigurationService =
            new PromotionConfigurationService(null, companyClientMappingRepository, clientRepository, null, null) {
                @Override
                String getTranslation(Translations.Message message) {
                    return message.name();
                }
            };

        ServiceResult<List<PromotionConfiguration>> serviceResult = promotionConfigurationService.getByCompanyIdRequiredPoints(1, "12345");
        assertNotNull(serviceResult);
        assertFalse(serviceResult.isSuccess());
        assertEquals(Translations.Message.PHONE_NUMBER_DOES_NOT_EXIST.name(), serviceResult.getMessage());

        verify(companyClientMappingRepository, clientRepository);
    }

    @Test
    public void testGetByCompanyIdRequiredPointsWithoutAvailablePromotions() throws Exception {
        List<PromotionConfiguration> expectedPromotionConfigurations = new ArrayList<>();
        expectedPromotionConfigurations.add(createPromotionConfiguration(1, 1, "5% off", 500));
        expectedPromotionConfigurations.add(createPromotionConfiguration(2, 1, "10% off", 1200));
        expectedPromotionConfigurations.add(createPromotionConfiguration(3, 1, "20% off", 2400));

        PromotionConfigurationRepository promotionConfigurationRepository =
            createPromotionConfigurationRepositoryForGet(expectedPromotionConfigurations);
        CompanyClientMapping companyClientMapping = new CompanyClientMapping();
        companyClientMapping.setPoints(300);
        ClientRepository clientRepository = createClientRepository(new Client());
        CompanyClientMappingRepository companyClientMappingRepository = createCompanyClientMappingRepository(companyClientMapping);
        PromotionConfigurationService promotionConfigurationService =
            new PromotionConfigurationService(promotionConfigurationRepository, companyClientMappingRepository, clientRepository, null, null) {
                @Override
                String getTranslation(Translations.Message message) {
                    return message.name();
                }
            };

        ServiceResult<List<PromotionConfiguration>> serviceResult = promotionConfigurationService.getByCompanyIdRequiredPoints(1, "12345");
        assertNotNull(serviceResult);
        assertTrue(serviceResult.isSuccess());
        assertEquals(Translations.Message.CLIENT_DOES_NOT_HAVE_AVAILABLE_PROMOTIONS.name(), serviceResult.getMessage());
        List<PromotionConfiguration> actualPromotionConfigurations = serviceResult.getObject();
        assertNotNull(actualPromotionConfigurations);
        assertEquals(0, actualPromotionConfigurations.size());

        verify(promotionConfigurationRepository, companyClientMappingRepository, clientRepository);
    }

    @Test
    public void testDeletePromotionConfiguration() throws Exception {
        PromotionConfigurationRepository promotionConfigurationRepository = createPromotionConfigurationRepositoryForDelete();

        PromotionConfigurationService promotionConfigurationService =
            new PromotionConfigurationService(promotionConfigurationRepository, null, null, null, null) {
                @Override
                String getTranslation(Translations.Message message) {
                    return message.name();
                }
            };

        final ServiceResult<Boolean> serviceResult = promotionConfigurationService.deletePromotionConfiguration(1);
        assertNotNull(serviceResult);
        assertTrue(serviceResult.isSuccess());
        assertEquals(Translations.Message.THE_PROMOTION_WAS_DELETED.name(), serviceResult.getMessage());
    }

    private CompanyClientMappingRepository createCompanyClientMappingRepository(CompanyClientMapping companyClientMapping) throws Exception {
        CompanyClientMappingRepository companyClientMappingRepository = createMock(CompanyClientMappingRepository.class);
        expect(companyClientMappingRepository.getByCompanyIdClientId(anyLong(), anyLong())).andReturn(companyClientMapping);
        replay(companyClientMappingRepository);
        return companyClientMappingRepository;
    }

    private ClientRepository createClientRepository(Client client) throws Exception {
        ClientRepository clientRepository = createMock(ClientRepository.class);
        expect(clientRepository.getByPhone(anyString())).andReturn(client);
        replay(clientRepository);
        return clientRepository;
    }

    private PromotionConfigurationRepository createPromotionConfigurationRepositoryForInsert() throws Exception {
        PromotionConfigurationRepository promotionConfigurationRepository = createMock(PromotionConfigurationRepository.class);
        expect(promotionConfigurationRepository.insert((PromotionConfiguration) anyObject())).andReturn(1l);
        replay(promotionConfigurationRepository);
        return promotionConfigurationRepository;
    }

    private PromotionConfigurationRepository createPromotionConfigurationRepositoryForGet(List<PromotionConfiguration> promotionConfigurations)
        throws Exception {
        PromotionConfigurationRepository promotionConfigurationRepository = createMock(PromotionConfigurationRepository.class);
        expect(promotionConfigurationRepository.getByCompanyId(anyLong())).andReturn(promotionConfigurations);
        replay(promotionConfigurationRepository);
        return promotionConfigurationRepository;
    }

    private PromotionConfigurationRepository createPromotionConfigurationRepositoryForDelete() throws Exception {
        PromotionConfigurationRepository promotionConfigurationRepository = createMock(PromotionConfigurationRepository.class);
        expect(promotionConfigurationRepository.delete(anyLong())).andReturn(1);
        replay(promotionConfigurationRepository);
        return promotionConfigurationRepository;
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
}
