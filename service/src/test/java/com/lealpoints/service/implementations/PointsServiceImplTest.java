package com.lealpoints.service.implementations;

import com.lealpoints.context.ThreadContextService;
import com.lealpoints.db.queryagent.QueryAgent;
import com.lealpoints.i18n.Message;
import com.lealpoints.model.Client;
import com.lealpoints.model.CompanyClientMapping;
import com.lealpoints.model.Points;
import com.lealpoints.model.PointsConfiguration;
import com.lealpoints.repository.ClientRepository;
import com.lealpoints.repository.CompanyClientMappingRepository;
import com.lealpoints.repository.PointsConfigurationRepository;
import com.lealpoints.repository.PointsRepository;
import com.lealpoints.service.PointsConfigurationService;
import com.lealpoints.service.model.PointsAwarding;
import com.lealpoints.service.model.ValidationResult;
import com.lealpoints.service.response.ServiceMessage;
import com.lealpoints.service.response.ServiceResult;
import org.easymock.EasyMockSupport;
import org.json.JSONObject;
import org.junit.Test;

import java.sql.SQLException;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class PointsServiceImplTest extends EasyMockSupport {

    private final PointsConfigurationServiceImpl pointsConfigurationService = createMock(PointsConfigurationServiceImpl.class);

    @Test
    public void testAwardPoints() throws Exception {
        //given
        expect(pointsConfigurationService.getByCompanyId(anyInt()))
                .andReturn(new ServiceResult<>(true, new ServiceMessage(""), createPointsConfiguration(10, 100)));
        PointsServiceImpl pointsService =
                new PointsServiceImpl(createPointsRepository(), null,
                        createClientRepository(), createCompanyClientMappingRepository(), createThreadContextService(createQueryAgent()),
                        createPhoneValidatorService(true, ServiceMessage.EMPTY), pointsConfigurationService) {
                    @Override
                    public ServiceMessage getServiceMessage(Message message, String... params) {
                        return new ServiceMessage(String.format(message.name(), params));
                    }
                };
        replayAll();
        PointsAwarding pointsAwarding = new PointsAwarding();
        pointsAwarding.setCompanyId(1);
        pointsAwarding.setPhoneNumber("12345");
        pointsAwarding.setSaleAmount(100);
        pointsAwarding.setSaleKey("A123");

        //when
        ServiceResult<Float> serviceResult = pointsService.awardPoints(pointsAwarding);

        //then
        assertNotNull(serviceResult);
        assertTrue(serviceResult.isSuccess());
        assertEquals(Message.POINTS_AWARDED.name(), serviceResult.getMessage());
        assertEquals(10, serviceResult.getObject(), 0.00);
        verifyAll();
    }

    @Test
    public void testAwardPointsWhenPhoneIsNotValid() throws Exception {
        PointsServiceImpl pointsService = new PointsServiceImpl(null, null, null, null, null,
                createPhoneValidatorService(false, new ServiceMessage(Message.PHONE_MUST_HAVE_10_DIGITS.name())), null) {

            @Override
            public ServiceMessage getServiceMessage(Message message, String... params) {
                return new ServiceMessage(message.name());
            }
        };
        replayAll();
        ServiceResult<Float> serviceResult = pointsService.awardPoints(new PointsAwarding());
        assertNotNull(serviceResult);
        assertFalse(serviceResult.isSuccess());
        assertEquals(Message.PHONE_MUST_HAVE_10_DIGITS.name(), serviceResult.getMessage());
        verifyAll();
    }

    @Test
    public void testAwardPointsWhenTheSaleKeyExists() throws Exception {
        PointsServiceImpl pointsService =
                new PointsServiceImpl(createPointsRepositoryWhenTheSaleKeyExists(), null, null, null, null,
                        createPhoneValidatorService(true, ServiceMessage.EMPTY), null) {
                    @Override
                    public ServiceMessage getServiceMessage(Message message, String... params) {
                        return new ServiceMessage(message.name());
                    }
                };
        replayAll();
        final PointsAwarding pointsAwarding = new PointsAwarding();
        pointsAwarding.setSaleKey("ABC");
        ServiceResult<Float> serviceResult = pointsService.awardPoints(pointsAwarding);
        assertNotNull(serviceResult);
        assertFalse(serviceResult.isSuccess());
        assertEquals(Message.SALE_KEY_ALREADY_EXISTS.name(), serviceResult.getMessage());
        verifyAll();
    }

    @Test
    public void testAwardPointsWhenTheSaleKeyIsEmpty() throws Exception {
        PointsServiceImpl pointsService =
                new PointsServiceImpl(null, null, null, null, null,
                        createPhoneValidatorService(true, ServiceMessage.EMPTY), null) {
                    @Override
                    public ServiceMessage getServiceMessage(Message message, String... params) {
                        return new ServiceMessage(message.name());
                    }
                };
        replayAll();
        ServiceResult<Float> serviceResult = pointsService.awardPoints(new PointsAwarding());
        assertNotNull(serviceResult);
        assertFalse(serviceResult.isSuccess());
        assertEquals(Message.EMPTY_SALE_KEY.name(), serviceResult.getMessage());
        verifyAll();
    }

    private PhoneValidatorServiceImpl createPhoneValidatorService(boolean isValid, ServiceMessage serviceMessage) {
        PhoneValidatorServiceImpl phoneValidatorService = createStrictMock(PhoneValidatorServiceImpl.class);
        expect(phoneValidatorService.validate(anyString())).andReturn(new ValidationResult(isValid, serviceMessage));
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
        expect(clientRepository.insertIfDoesNotExist(anyString(), anyBoolean()))
                .andReturn(new xyz.greatapp.libs.service.ServiceResult(true, "", new Client().toJSONObject().toString()));
        return clientRepository;
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
