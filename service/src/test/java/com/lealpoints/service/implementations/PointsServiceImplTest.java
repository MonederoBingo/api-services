package com.lealpoints.service.implementations;

import java.sql.SQLException;
import com.lealpoints.context.ThreadContextService;
import com.lealpoints.db.queryagent.QueryAgent;
import com.lealpoints.model.Client;
import com.lealpoints.model.CompanyClientMapping;
import com.lealpoints.model.Points;
import com.lealpoints.model.PointsConfiguration;
import com.lealpoints.repository.ClientRepository;
import com.lealpoints.repository.CompanyClientMappingRepository;
import com.lealpoints.repository.PointsConfigurationRepository;
import com.lealpoints.repository.PointsRepository;
import com.lealpoints.service.model.PointsAwarding;
import com.lealpoints.service.model.ServiceResult;
import com.lealpoints.service.model.ValidationResult;
import com.lealpoints.util.Translations;
import org.easymock.EasyMockSupport;
import org.junit.Test;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class PointsServiceImplTest extends EasyMockSupport {

    @Test
    public void testAwardPoints() throws Exception {
        PointsServiceImpl pointsService =
            new PointsServiceImpl(createPointsRepository(), createPointsConfigurationRepository(createPointsConfiguration(10, 100)),
                createClientRepository(), createCompanyClientMappingRepository(), createThreadContextService(createQueryAgent()), null,
                createPhoneValidatorService(true, "")) {
                @Override
                public String getTranslation(Translations.Message message) {
                    return message.name();
                }
            };
        replayAll();
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
        verifyAll();
    }

    @Test
    public void testAwardPointsWhenTheSaleKeyExists() throws Exception {
        PointsServiceImpl pointsService = new PointsServiceImpl(null, null, null, null, null, null,
            createPhoneValidatorService(false, Translations.Message.PHONE_MUST_HAVE_10_DIGITS.name())) {

            @Override
            public String getTranslation(Translations.Message message) {
                return message.name();
            }
        };
        replayAll();
        ServiceResult<Float> serviceResult = pointsService.awardPoints(new PointsAwarding());
        assertNotNull(serviceResult);
        assertFalse(serviceResult.isSuccess());
        assertEquals(Translations.Message.PHONE_MUST_HAVE_10_DIGITS.name(), serviceResult.getMessage());
        verifyAll();
    }

    @Test
    public void testAwardPointsWhenPhoneIsNotValid() throws Exception {
        PointsServiceImpl pointsService =
            new PointsServiceImpl(createPointsRepositoryWhenTheSaleKeyExists(), null, null, null, null, null, createPhoneValidatorService(true, "")) {

                @Override
                public String getTranslation(Translations.Message message) {
                    return message.name();
                }
            };
        replayAll();
        ServiceResult<Float> serviceResult = pointsService.awardPoints(new PointsAwarding());
        assertNotNull(serviceResult);
        assertFalse(serviceResult.isSuccess());
        assertEquals(Translations.Message.SALE_KEY_ALREADY_EXISTS.name(), serviceResult.getMessage());
        verifyAll();
    }

    private PhoneValidatorServiceImpl createPhoneValidatorService(boolean isValid, String message) {
        PhoneValidatorServiceImpl phoneValidatorService = createStrictMock(PhoneValidatorServiceImpl.class);
        expect(phoneValidatorService.validate(anyString())).andReturn(new ValidationResult(isValid, message));
        return phoneValidatorService;
    }

    private PointsConfiguration createPointsConfiguration(int pointsToEarn, int requiredAmount) {
        final PointsConfiguration pointsConfiguration = new PointsConfiguration();
        pointsConfiguration.setRequiredAmount(requiredAmount);
        pointsConfiguration.setPointsToEarn(pointsToEarn);
        return pointsConfiguration;
    }

    private ThreadContextService createThreadContextService(QueryAgent queryAgent) throws SQLException {
        ThreadContextService threadContextService = createMock(ThreadContextService.class);
        expect(threadContextService.getQueryAgent()).andReturn(queryAgent).times(1);
        return threadContextService;
    }

    private QueryAgent createQueryAgent() throws Exception {
        QueryAgent queryAgent = createMock(QueryAgent.class);
        queryAgent.beginTransaction();
        expectLastCall().times(1);
        queryAgent.commitTransaction();
        expectLastCall().times(1);
        return queryAgent;
    }

    private CompanyClientMappingRepository createCompanyClientMappingRepository() throws Exception {
        CompanyClientMappingRepository companyClientMappingRepository = createMock(CompanyClientMappingRepository.class);
        expect(companyClientMappingRepository.insertIfDoesNotExist(anyLong(), anyLong())).andReturn(new CompanyClientMapping());
        expect(companyClientMappingRepository.getByCompanyIdClientId(anyLong(), anyLong())).andReturn(new CompanyClientMapping());
        expect(companyClientMappingRepository.updatePoints((CompanyClientMapping) anyObject())).andReturn(1);
        return companyClientMappingRepository;
    }

    private ClientRepository createClientRepository() throws Exception {
        ClientRepository clientRepository = createMock(ClientRepository.class);
        expect(clientRepository.insertIfDoesNotExist(anyString(), anyBoolean())).andReturn(new Client());
        return clientRepository;
    }

    private PointsConfigurationRepository createPointsConfigurationRepository(PointsConfiguration pointsConfiguration) throws Exception {
        PointsConfigurationRepository pointsConfigurationRepository = createMock(PointsConfigurationRepository.class);
        expect(pointsConfigurationRepository.getByCompanyId(anyLong())).andReturn(pointsConfiguration);
        return pointsConfigurationRepository;
    }

    private PointsRepository createPointsRepository() throws Exception {
        PointsRepository pointsRepository = createMock(PointsRepository.class);
        expect(pointsRepository.insert((Points) anyObject())).andReturn(1l);
        expect(pointsRepository.getByCompanyIdSaleKey(anyLong(), anyString())).andReturn(null);
        return pointsRepository;
    }

    private PointsRepository createPointsRepositoryWhenTheSaleKeyExists() throws Exception {
        PointsRepository pointsRepository = createMock(PointsRepository.class);
        expect(pointsRepository.getByCompanyIdSaleKey(anyLong(), anyString())).andReturn(new Points());
        return pointsRepository;
    }
}