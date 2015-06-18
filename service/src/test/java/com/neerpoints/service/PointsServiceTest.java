package com.neerpoints.service;

import java.sql.SQLException;
import com.neerpoints.context.ThreadContextService;
import com.neerpoints.db.QueryAgent;
import com.neerpoints.model.Client;
import com.neerpoints.model.CompanyClientMapping;
import com.neerpoints.model.Points;
import com.neerpoints.model.PointsConfiguration;
import com.neerpoints.repository.ClientRepository;
import com.neerpoints.repository.CompanyClientMappingRepository;
import com.neerpoints.repository.PointsConfigurationRepository;
import com.neerpoints.repository.PointsRepository;
import com.neerpoints.service.model.PointsAwarding;
import com.neerpoints.service.model.ServiceResult;
import com.neerpoints.util.Translations;
import org.junit.Test;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class PointsServiceTest {

    @Test
    public void testAwardPoints() throws Exception {
        PointsRepository pointsRepository = createPointsRepository();
        final PointsConfiguration pointsConfiguration = new PointsConfiguration();
        pointsConfiguration.setRequiredAmount(100);
        pointsConfiguration.setPointsToEarn(10);
        PointsConfigurationRepository pointsConfigurationRepository = createPointsConfigurationRepository(pointsConfiguration);
        ClientRepository clientRepository = createClientRepository();
        CompanyClientMappingRepository companyClientMappingRepository = createCompanyClientMappingRepository();
        final QueryAgent queryAgent = createQueryAgent();
        ThreadContextService threadContextService = createThreadContextService(queryAgent);
        PointsService pointsService =
            new PointsService(pointsRepository, pointsConfigurationRepository, clientRepository, companyClientMappingRepository, threadContextService,
                null) {
                @Override
                String getTranslation(Translations.Message message) {
                    return message.name();
                }
            };
        PointsAwarding pointsAwarding = new PointsAwarding();
        pointsAwarding.setCompanyId(1);
        pointsAwarding.setPhone("12345");
        pointsAwarding.setSaleAmount(100);
        pointsAwarding.setSaleKey("A123");

        ServiceResult<Float> serviceResult = pointsService.awardPoints(pointsAwarding);
        assertNotNull(serviceResult);
        assertTrue(serviceResult.isSuccess());
        assertEquals(Translations.Message.POINTS_AWARDED.name() + ": " + 10.0, serviceResult.getMessage());
        assertEquals(10, serviceResult.getObject(), 0.00);
        verify(pointsRepository, pointsConfigurationRepository, clientRepository, companyClientMappingRepository, queryAgent, threadContextService);
    }

    @Test
    public void testAwardPointsWhenNotEnoughSaleAmount() throws Exception {
        PointsRepository pointsRepository = createPointsRepositoryWhenNotEnoughSaleAmount();
        final PointsConfiguration pointsConfiguration = new PointsConfiguration();
        pointsConfiguration.setRequiredAmount(100);
        pointsConfiguration.setPointsToEarn(10);
        PointsConfigurationRepository pointsConfigurationRepository = createPointsConfigurationRepository(pointsConfiguration);
        final QueryAgent queryAgent = createQueryAgent();
        ThreadContextService threadContextService = createThreadContextService(queryAgent);
        PointsService pointsService = new PointsService(pointsRepository, pointsConfigurationRepository, null, null, threadContextService, null) {
            @Override
            String getTranslation(Translations.Message message) {
                return message.name();
            }
        };
        PointsAwarding pointsAwarding = new PointsAwarding();
        pointsAwarding.setCompanyId(1);
        pointsAwarding.setPhone("12345");
        pointsAwarding.setSaleAmount(60);
        pointsAwarding.setSaleKey("A123");

        ServiceResult<Float> serviceResult = pointsService.awardPoints(pointsAwarding);
        assertNotNull(serviceResult);
        assertTrue(serviceResult.isSuccess());
        assertEquals(Translations.Message.THE_CLIENT_DID_NOT_GET_POINTS.name(), serviceResult.getMessage());
        assertEquals(0, serviceResult.getObject(), 0.00);
        verify(pointsRepository, pointsConfigurationRepository, queryAgent, threadContextService);
    }

    @Test
    public void testAwardPointsWhenTheSaleKeyExists() throws Exception {
        PointsRepository pointsRepository = createPointsRepositoryWhenTheSaleKeyExists();
        PointsService pointsService = new PointsService(pointsRepository, null, null, null, null, null) {
            @Override
            String getTranslation(Translations.Message message) {
                return message.name();
            }
        };
        ServiceResult<Float> serviceResult = pointsService.awardPoints(new PointsAwarding());
        assertNotNull(serviceResult);
        assertFalse(serviceResult.isSuccess());
        assertEquals(Translations.Message.SALE_KEY_ALREADY_EXISTS.name(), serviceResult.getMessage());
        verify(pointsRepository);
    }

    private ThreadContextService createThreadContextService(QueryAgent queryAgent) throws SQLException {
        ThreadContextService threadContextService = createMock(ThreadContextService.class);

        expect(threadContextService.getQueryAgent()).andReturn(queryAgent).times(1);
        replay(threadContextService);
        return threadContextService;
    }

    private QueryAgent createQueryAgent() throws Exception {
        QueryAgent queryAgent = createMock(QueryAgent.class);
        queryAgent.beginTransaction();
        expectLastCall().times(1);
        queryAgent.commitTransaction();
        expectLastCall().times(1);
        replay(queryAgent);
        return queryAgent;
    }

    private CompanyClientMappingRepository createCompanyClientMappingRepository() throws Exception {
        CompanyClientMappingRepository companyClientMappingRepository = createMock(CompanyClientMappingRepository.class);
        expect(companyClientMappingRepository.insertIfDoesNotExist(anyLong(), anyLong())).andReturn(new CompanyClientMapping());
        expect(companyClientMappingRepository.getByCompanyIdClientId(anyLong(), anyLong())).andReturn(new CompanyClientMapping());
        expect(companyClientMappingRepository.updatePoints((CompanyClientMapping) anyObject())).andReturn(1);
        replay(companyClientMappingRepository);
        return companyClientMappingRepository;
    }

    private ClientRepository createClientRepository() throws Exception {
        ClientRepository clientRepository = createMock(ClientRepository.class);
        expect(clientRepository.insertIfDoesNotExist(anyString())).andReturn(new Client());
        replay(clientRepository);
        return clientRepository;
    }

    private PointsConfigurationRepository createPointsConfigurationRepository(PointsConfiguration pointsConfiguration) throws Exception {
        PointsConfigurationRepository pointsConfigurationRepository = createMock(PointsConfigurationRepository.class);
        expect(pointsConfigurationRepository.getByCompanyId(anyLong())).andReturn(pointsConfiguration);
        replay(pointsConfigurationRepository);
        return pointsConfigurationRepository;
    }

    private PointsRepository createPointsRepository() throws Exception {
        PointsRepository pointsRepository = createMock(PointsRepository.class);
        expect(pointsRepository.insert((Points) anyObject())).andReturn(1l);
        expect(pointsRepository.getByCompanyIdSaleKey(anyLong(), anyString())).andReturn(null);
        replay(pointsRepository);
        return pointsRepository;
    }

    private PointsRepository createPointsRepositoryWhenTheSaleKeyExists() throws Exception {
        PointsRepository pointsRepository = createMock(PointsRepository.class);
        expect(pointsRepository.getByCompanyIdSaleKey(anyLong(), anyString())).andReturn(new Points());
        replay(pointsRepository);
        return pointsRepository;
    }

    private PointsRepository createPointsRepositoryWhenNotEnoughSaleAmount() throws Exception {
        PointsRepository pointsRepository = createMock(PointsRepository.class);
        expect(pointsRepository.getByCompanyIdSaleKey(anyLong(), anyString())).andReturn(null);
        replay(pointsRepository);
        return pointsRepository;
    }
}