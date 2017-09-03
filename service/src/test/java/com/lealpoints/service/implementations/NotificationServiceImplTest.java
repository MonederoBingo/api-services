package com.lealpoints.service.implementations;

import com.lealpoints.i18n.Message;
import com.lealpoints.model.Client;
import com.lealpoints.model.Company;
import com.lealpoints.model.CompanyClientMapping;
import com.lealpoints.repository.ClientRepository;
import com.lealpoints.repository.CompanyClientMappingRepository;
import com.lealpoints.repository.CompanyRepository;
import com.lealpoints.service.response.ServiceMessage;
import com.lealpoints.service.response.ServiceResult;
import org.junit.Test;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class NotificationServiceImplTest extends ServiceBaseTest {

    @Test
    public void testSendMobileAppAdMessage() throws Exception {
        //given
        SMSServiceImpl smsService = createStrictMock(SMSServiceImpl.class);
        smsService.sendSMSMessage(anyString(), anyString());
        expectLastCall();

        CompanyRepository companyRepository = createNiceMock(CompanyRepository.class);
        expect(companyRepository.getByCompanyId(anyLong()))
                .andReturn(new xyz.greatapp.libs.service.ServiceResult(true, "", new Company().toJSONObject().toString()));

        CompanyClientMappingRepository companyClientMappingRepository = createNiceMock(CompanyClientMappingRepository.class);
        expect(companyClientMappingRepository.getByCompanyIdClientId(anyLong(), anyLong()))
                .andReturn(new xyz.greatapp.libs.service.ServiceResult(true, "", new CompanyClientMapping().toJSONObject().toString()));

        ClientRepository clientRepository = createStrictMock(ClientRepository.class);
        expect(clientRepository.getByPhone(anyString())).andReturn(new xyz.greatapp.libs.service.ServiceResult(true, "", new Client().toJSONObject().toString()));
        expect(clientRepository.updateCanReceivePromoSms(anyLong(), anyBoolean())).andReturn(1);

        replay(smsService, companyRepository, clientRepository, companyClientMappingRepository);

        NotificationServiceImpl notificationService = new NotificationServiceImpl(null,
                companyRepository, clientRepository, companyClientMappingRepository, smsService) {
            @Override
            public ServiceMessage getServiceMessage(Message message, String... params) {
                return new ServiceMessage(message.name());
            }
        };

        //when
        final ServiceResult serviceResult = notificationService.sendMobileAppAdMessage(0, "6623471507");

        //then
        assertTrue(serviceResult.isSuccess());
        assertEquals(Message.MOBILE_APP_AD_MESSAGE_SENT_SUCCESSFULLY.name(), serviceResult.getMessage());

        verify(smsService, companyRepository, clientRepository, companyClientMappingRepository);
    }

    @Test
    public void testSendMobileAppAdMessageWhenIsNotProdEnv() throws Exception {
        ConfigurationServiceImpl configurationManager = createStrictMock(ConfigurationServiceImpl.class);
        replay(configurationManager);
        NotificationServiceImpl notificationService = new NotificationServiceImpl(null, null, null, null, null) {
            @Override
            public ServiceMessage getServiceMessage(Message message, String... params) {
                return new ServiceMessage(message.name());
            }

            @Override
            public boolean isProdEnvironment() {
                return false;
            }
        };
        final ServiceResult serviceResult = notificationService.sendMobileAppAdMessage(0, "6623471507");
        assertFalse(serviceResult.isSuccess());
        assertEquals(Message.MOBILE_APP_AD_MESSAGE_WAS_NOT_SENT_SUCCESSFULLY.name(), serviceResult.getMessage());
        verify(configurationManager);
    }

    @Test
    public void testSendMobileAppAdMessageWhenConfIsEnabled() throws Exception {
        //given
        SMSServiceImpl smsService = createStrictMock(SMSServiceImpl.class);
        smsService.sendSMSMessage(anyString(), anyString());
        expectLastCall();

        CompanyRepository companyRepository = createNiceMock(CompanyRepository.class);
        expect(companyRepository.getByCompanyId(anyLong()))
                .andReturn(new xyz.greatapp.libs.service.ServiceResult(true, "", new Company().toJSONObject().toString()));

        CompanyClientMappingRepository companyClientMappingRepository = createNiceMock(CompanyClientMappingRepository.class);
        expect(companyClientMappingRepository.getByCompanyIdClientId(anyLong(), anyLong()))
                .andReturn(new xyz.greatapp.libs.service.ServiceResult(true, "", new CompanyClientMapping().toJSONObject().toString()));

        ClientRepository clientRepository = createStrictMock(ClientRepository.class);
        expect(clientRepository.getByPhone(anyString()))
                .andReturn(new xyz.greatapp.libs.service.ServiceResult(true, "", new Client().toJSONObject().toString()));
        expect(clientRepository.updateCanReceivePromoSms(anyLong(), anyBoolean())).andReturn(1);

        replay(smsService, companyRepository, clientRepository, companyClientMappingRepository);

        NotificationServiceImpl notificationService = new NotificationServiceImpl(null, companyRepository, clientRepository,
                companyClientMappingRepository, smsService) {
            @Override
            public ServiceMessage getServiceMessage(Message message, String... params) {
                return new ServiceMessage(message.name());
            }

            @Override
            public boolean isProdEnvironment() {
                return false;
            }
        };

        //when
        final ServiceResult serviceResult = notificationService.sendMobileAppAdMessage(0, "6623471507");

        //then
        assertTrue(serviceResult.isSuccess());
        assertEquals(Message.MOBILE_APP_AD_MESSAGE_SENT_SUCCESSFULLY.name(), serviceResult.getMessage());
        verify(smsService, clientRepository);
    }

    @Test
    public void testGetSMSMessageWithInvalidTranslation() {
        NotificationServiceImpl notificationService = new NotificationServiceImpl(null, null, null, null, null) {
            @Override
            public ServiceMessage getServiceMessage(Message message, String... params) {
                return new ServiceMessage("You've got %s points at %s. Install Monedero Bingo to see our promotions and much much much much much much much much much " +
                        "much much much much much much more. %s");
            }
        };
        try {
            notificationService.getSMSMessage(1000);
        } catch (IllegalArgumentException e) {
            assertEquals(
                    "Message length must be less than 160 in: You've got %s points at %s. Install Monedero Bingo to see our promotions and much much " +
                            "much much much much much much much much much much much much much more. %s", e.getMessage());
        }

        notificationService = new NotificationServiceImpl(null, null, null, null, null) {
            @Override
            public ServiceMessage getServiceMessage(Message message, String... params) {
                return new ServiceMessage("You've got %s points at %s. Install Monedero Bingo to see our promotions and much much much much much much much much much much " +
                        "much much much much much much much much much much much much much much much much much much much much more. %s");
            }
        };
        try {
            notificationService.getSMSMessage(1000);
        } catch (IllegalArgumentException e) {
            assertEquals("Message length must be less than 160 in: You've got %s points at %s. Install Monedero Bingo to see our promotions and much " +
                    "much much much much much much much much much much much much much much much much much much much much much much much much much much " +
                    "much much much more. %s", e.getMessage());
        }
    }
}