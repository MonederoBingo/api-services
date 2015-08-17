package com.lealpoints.service;

import java.sql.SQLException;
import com.lealpoints.context.ThreadContextService;
import com.lealpoints.db.QueryAgent;
import com.lealpoints.model.Client;
import com.lealpoints.model.CompanyClientMapping;
import com.lealpoints.model.Promotion;
import com.lealpoints.model.PromotionConfiguration;
import com.lealpoints.repository.ClientRepository;
import com.lealpoints.repository.CompanyClientMappingRepository;
import com.lealpoints.repository.PromotionConfigurationRepository;
import com.lealpoints.repository.PromotionRepository;
import com.lealpoints.service.model.PromotionApplying;
import com.lealpoints.service.model.ServiceResult;
import com.lealpoints.util.Translations;
import org.junit.Test;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class PromotionServiceTest {

    @Test
    public void testApplyPromotion() throws Exception {

        PromotionRepository promotionRepository = createPromotionRepository();
        PromotionConfigurationRepository promotionConfigurationRepository = createPromotionConfigurationRepository();
        ClientRepository clientRepository = createClientRepository();
        final QueryAgent queryAgent = createQueryAgent();
        ThreadContextService threadContextService = createThreadContextService(queryAgent);
        CompanyClientMappingRepository companyClientMappingRepository = createCompanyClientMappingRepository();
        PromotionService promotionService =
            new PromotionService(promotionRepository, promotionConfigurationRepository, companyClientMappingRepository, clientRepository,
                threadContextService, null) {
                @Override
                protected String getTranslation(Translations.Message message) {
                    return message.name();
                }
            };
        PromotionApplying promotionApplying = new PromotionApplying();
        promotionApplying.setPromotionConfigurationId(1);
        promotionApplying.setCompanyId(1);
        promotionApplying.setPhone("1234567890");

        ServiceResult<Long> serviceResult = promotionService.applyPromotion(promotionApplying);
        assertNotNull(serviceResult);
        assertTrue(serviceResult.isSuccess());
        assertEquals(Translations.Message.PROMOTION_APPLIED.name(), serviceResult.getMessage());
        assertTrue(serviceResult.getObject() > 0);
        verify(promotionRepository, promotionConfigurationRepository, companyClientMappingRepository, queryAgent, threadContextService);
    }

    private CompanyClientMappingRepository createCompanyClientMappingRepository() throws Exception {
        CompanyClientMappingRepository companyClientMappingRepository = createMock(CompanyClientMappingRepository.class);
        expect(companyClientMappingRepository.getByCompanyIdClientId(anyLong(), anyLong())).andReturn(new CompanyClientMapping());
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

    private QueryAgent createQueryAgent() throws Exception {
        QueryAgent queryAgent = createMock(QueryAgent.class);
        queryAgent.beginTransaction();
        expectLastCall().times(1);
        queryAgent.commitTransaction();
        expectLastCall().times(1);
        replay(queryAgent);
        return queryAgent;
    }

    private ClientRepository createClientRepository() throws Exception {
        ClientRepository clientRepository = createMock(ClientRepository.class);
        expect(clientRepository.getByPhone(anyString())).andReturn(new Client());
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
        expect(promotionRepository.insert((Promotion) anyObject())).andReturn(1l);
        replay(promotionRepository);
        return promotionRepository;
    }
}