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
        SMSServiceImpl smsService = createStrictMock(SMSServiceImpl.class);
        smsService.sendSMSMessage(anyString(), anyString());
        expectLastCall();

        CompanyRepository companyRepository = createNiceMock(CompanyRepository.class);
        expect(companyRepository.getByCompanyId(anyLong())).andReturn(new Company());

        CompanyClientMappingRepository companyClientMappingRepository = createNiceMock(CompanyClientMappingRepository.class);
        expect(companyClientMappingRepository.getByCompanyIdClientId(anyLong(), anyLong())).andReturn(new CompanyClientMapping());

        ClientRepository clientRepository = createStrictMock(ClientRepository.class);
        expect(clientRepository.getByPhone(anyString())).andReturn(new Client());
        expect(clientRepository.updateCanReceivePromoSms(anyLong(), anyBoolean())).andReturn(1);

        replay(smsService, companyRepository, clientRepository, companyClientMappingRepository);

        NotificationServiceImpl notificationService = new NotificationServiceImpl(null,
                companyRepository, clientRepository, companyClientMappingRepository, smsService) {
            @Override
            public ServiceMessage getServiceMessage(Message message, String... params) {
                return new ServiceMessage(message.name());
            }
        };

        final ServiceResult serviceResult = notificationService.sendMobileAppAdMessage(0, "6623471507");
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
        SMSServiceImpl smsService = createStrictMock(SMSServiceImpl.class);
        smsService.sendSMSMessage(anyString(), anyString());
        expectLastCall();

        CompanyRepository companyRepository = createNiceMock(CompanyRepository.class);
        expect(companyRepository.getByCompanyId(anyLong())).andReturn(new Company());

        CompanyClientMappingRepository companyClientMappingRepository = createNiceMock(CompanyClientMappingRepository.class);
        expect(companyClientMappingRepository.getByCompanyIdClientId(anyLong(), anyLong())).andReturn(new CompanyClientMapping());

        ClientRepository clientRepository = createStrictMock(ClientRepository.class);
        expect(clientRepository.getByPhone(anyString())).andReturn(new Client());
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
        final ServiceResult serviceResult = notificationService.sendMobileAppAdMessage(0, "6623471507");
        assertTrue(serviceResult.isSuccess());
        assertEquals(Message.MOBILE_APP_AD_MESSAGE_SENT_SUCCESSFULLY.name(), serviceResult.getMessage());
        verify(smsService, clientRepository);
    }

    @Test
    public void testGetSMSMessage() {
        NotificationServiceImpl notificationService = new NotificationServiceImpl(null, null, null, null, null) {
            @Override
            public ServiceMessage getServiceMessage(Message message, String... params) {
                return new ServiceMessage("You've got %s points at %s. Install Monedero Bingo to see our promotions. %s");
            }
        };
        String smsMessage = notificationService.getSMSMessage("New Company From an Awesome Place and a Big Name", 1000);
        assertNotNull(smsMessage);
        assertEquals("You've got 1000 points at New Company From an Awesome Place and a Big Name. Install Monedero Bingo to see our promotions. " +
                "https://goo.gl/JRssA6", smsMessage);

        notificationService = new NotificationServiceImpl(null, null, null, null, null) {
            @Override
            public ServiceMessage getServiceMessage(Message message, String... params) {
                return new ServiceMessage("You've got %s points at %s. Install Monedero Bingo to see our promotions. %s");
            }
        };
        smsMessage = notificationService.getSMSMessage("TG", 1000);
        assertNotNull(smsMessage);
        assertEquals("You've got 1000 points at TG. Install Monedero Bingo to see our promotions. " + "https://goo.gl/JRssA6", smsMessage);

        notificationService = new NotificationServiceImpl(null, null, null, null, null) {
            @Override
            public ServiceMessage getServiceMessage(Message message, String... params) {
                return new ServiceMessage("You've got %s points at %s. Install Monedero Bingo to see our promotions. %s");
            }
        };
        smsMessage = notificationService.getSMSMessage("New Company From an Awesome Place and a Big Name that does not fit in the message", 1000);
        assertNotNull(smsMessage);
        assertEquals("You've got 1000 points at New Company From an Awesome Place and a Big Name that does n.... " +
                "Install Monedero Bingo to see our promotions. https://goo.gl/JRssA6", smsMessage);
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
            notificationService.getSMSMessage("New Company From an Awesome Place and a Big Name that does not fit in the message", 1000);
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
            notificationService.getSMSMessage("TG", 1000);
        } catch (IllegalArgumentException e) {
            assertEquals("Message length must be less than 160 in: You've got %s points at %s. Install Monedero Bingo to see our promotions and much " +
                    "much much much much much much much much much much much much much much much much much much much much much much much much much much " +
                    "much much much more. %s", e.getMessage());
        }
    }
}