package com.lealpoints.service.implementations;

import com.lealpoints.context.ThreadContextService;
import com.lealpoints.db.queryagent.QueryAgent;
import com.lealpoints.i18n.Message;
import com.lealpoints.model.Client;
import com.lealpoints.model.CompanyClientMapping;
import com.lealpoints.model.Promotion;
import com.lealpoints.model.PromotionConfiguration;
import com.lealpoints.repository.ClientRepository;
import com.lealpoints.repository.CompanyClientMappingRepository;
import com.lealpoints.repository.PromotionConfigurationRepository;
import com.lealpoints.repository.PromotionRepository;
import com.lealpoints.service.model.PromotionApplying;
import com.lealpoints.service.response.ServiceMessage;
import com.lealpoints.service.response.ServiceResult;
import org.junit.Test;

import java.sql.SQLException;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class PromotionServiceImplTest extends BaseServiceTest {

    @Test
    public void testApplyPromotion() throws Exception {
        //given
        PromotionRepository promotionRepository = createPromotionRepository();
        PromotionConfigurationRepository promotionConfigurationRepository = createPromotionConfigurationRepository();
        ClientRepository clientRepository = createClientRepository();
        final QueryAgent queryAgent = createQueryAgent();
        ThreadContextService threadContextService = createThreadContextService(queryAgent);
        CompanyClientMappingRepository companyClientMappingRepository = createCompanyClientMappingRepository();
        PromotionServiceImpl promotionService =
                new PromotionServiceImpl(promotionRepository, promotionConfigurationRepository, companyClientMappingRepository, clientRepository,
                        threadContextService) {
                    @Override
                    public ServiceMessage getServiceMessage(Message message, String... params) {
                        return new ServiceMessage(message.name());
                    }
                };
        PromotionApplying promotionApplying = new PromotionApplying();
        promotionApplying.setPromotionConfigurationId(1);
        promotionApplying.setCompanyId(1);
        promotionApplying.setPhoneNumber("1234567890");

        //when
        ServiceResult<Long> serviceResult = promotionService.applyPromotion(promotionApplying);

        //then
        assertNotNull(serviceResult);
        assertTrue(serviceResult.isSuccess());
        assertEquals(Message.PROMOTION_APPLIED.name(), serviceResult.getMessage());
        assertTrue(serviceResult.getObject() > 0);
        verify(promotionRepository, promotionConfigurationRepository, companyClientMappingRepository, queryAgent, threadContextService);
    }

    private CompanyClientMappingRepository createCompanyClientMappingRepository() throws Exception {
        CompanyClientMappingRepository companyClientMappingRepository = createMock(CompanyClientMappingRepository.class);
        CompanyClientMapping companyClientMapping = new CompanyClientMapping();
        companyClientMapping.setClient(new Client());
        expect(companyClientMappingRepository.getByCompanyIdClientId(anyLong(), anyLong()))
                .andReturn(new xyz.greatapp.libs.service.ServiceResult(true, "", companyClientMapping.toJSONObject().toString()));
        expect(companyClientMappingRepository.updatePoints((CompanyClientMapping) anyObject())).andReturn(1);
        replay(companyClientMappingRepository);
        return companyClientMappingRepository;
    }

    private ThreadContextService createThreadContextService(QueryAgent queryAgent) throws SQLException {
        ThreadContextService threadContextService = createMock(ThreadContextService.class);

        expect(threadContextService.getQueryAgent()).andReturn(queryAgent).times(1);
        replay(threadContextService);
        return threadContextService;
    }

    private ClientRepository createClientRepository() throws Exception {
        ClientRepository clientRepository = createMock(ClientRepository.class);
        expect(clientRepository.getByPhone(anyString()))
                .andReturn(new xyz.greatapp.libs.service.ServiceResult(true, "", new Client().toJSONObject().toString()));
        replay(clientRepository);
        return clientRepository;
    }

    private PromotionConfigurationRepository createPromotionConfigurationRepository() throws Exception {
        PromotionConfigurationRepository promotionConfigurationRepository = createMock(PromotionConfigurationRepository.class);
        expect(promotionConfigurationRepository.getById(anyLong())).andReturn(new PromotionConfiguration());
        replay(promotionConfigurationRepository);
        return promotionConfigurationRepository;
    }

    private PromotionRepository createPromotionRepository() throws Exception {
        PromotionRepository promotionRepository = createMock(PromotionRepository.class);
        expect(promotionRepository.insert((Promotion) anyObject())).andReturn(1L);
        replay(promotionRepository);
        return promotionRepository;
    }
}