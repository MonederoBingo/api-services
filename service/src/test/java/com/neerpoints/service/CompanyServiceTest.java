package com.neerpoints.service;

import javax.mail.MessagingException;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.neerpoints.context.ThreadContext;
import com.neerpoints.context.ThreadContextService;
import com.neerpoints.db.QueryAgent;
import com.neerpoints.model.Client;
import com.neerpoints.model.Company;
import com.neerpoints.model.CompanyClientMapping;
import com.neerpoints.model.CompanyUser;
import com.neerpoints.model.PointsConfiguration;
import com.neerpoints.model.PointsInCompany;
import com.neerpoints.repository.ClientRepository;
import com.neerpoints.repository.CompanyClientMappingRepository;
import com.neerpoints.repository.CompanyRepository;
import com.neerpoints.repository.CompanyUserRepository;
import com.neerpoints.repository.PointsConfigurationRepository;
import com.neerpoints.service.model.CompanyRegistration;
import com.neerpoints.service.model.ServiceResult;
import com.neerpoints.util.Translations;
import org.apache.commons.fileupload.FileItem;
import org.easymock.EasyMock;
import org.junit.Ignore;
import org.junit.Test;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class CompanyServiceTest {
    @Test
    public void testRegister() throws Exception {
        final CompanyRepository companyRepository = createCompanyRepository();
        final CompanyUserRepository companyUserRepository = createCompanyUserRepository();
        PointsConfigurationRepository pointsConfigurationRepository = createPointsConfigurationRepository();
        final QueryAgent queryAgent = createQueryAgent();
        ThreadContext threadContext = new ThreadContext();
        threadContext.setProdEnvironment(false);
        final ThreadContextService threadContextService = createThreadContextService(queryAgent);
        CompanyService companyService =
            createCompanyService(companyRepository, companyUserRepository, pointsConfigurationRepository, threadContextService, null);

        final CompanyRegistration companyRegistration = new CompanyRegistration();
        companyRegistration.setCompanyName("company name");
        companyRegistration.setUserName("user name");
        companyRegistration.setEmail("email@test.com");
        companyRegistration.setPassword("Pa$$w0rd");
        companyRegistration.setPasswordConfirmation("Pa$$w0rd");
        companyRegistration.setUrlImageLogo("images/logo.png");

        ServiceResult serviceResult = companyService.register(companyRegistration);
        assertNotNull(serviceResult);
        assertTrue(serviceResult.isSuccess());
        assertNotNull(serviceResult.getMessage());
        assertEquals(Translations.Message.WE_HAVE_SENT_YOU_AND_ACTIVATION_LINK.name(), serviceResult.getMessage());
        verify(companyRepository, companyUserRepository, threadContextService, queryAgent, pointsConfigurationRepository);
    }

    @Test
    public void testRegisterWhenPasswordIsDifferentFromConfirmation() throws Exception {
        final CompanyService companyService = createCompanyService(null, null, null, null, null);
        final CompanyRegistration companyRegistration = new CompanyRegistration();
        companyRegistration.setPassword("123456");
        companyRegistration.setPasswordConfirmation("123457");
        ServiceResult serviceResult = companyService.register(companyRegistration);
        assertNotNull(serviceResult);
        assertFalse(serviceResult.isSuccess());
        assertEquals(Translations.Message.PASSWORD_AND_CONFIRMATION_ARE_DIFFERENT.name(), serviceResult.getMessage());
        assertNull(serviceResult.getObject());
    }

    @Test
    public void testRegisterWhenPasswordIsShort() throws Exception {
        CompanyService companyService = createCompanyService(null, null, null, null, null);
        final CompanyRegistration companyRegistration = new CompanyRegistration();
        companyRegistration.setPassword("12345");
        companyRegistration.setPasswordConfirmation("12345");
        ServiceResult serviceResult = companyService.register(companyRegistration);
        assertNotNull(serviceResult);
        assertFalse(serviceResult.isSuccess());
        assertEquals(Translations.Message.PASSWORD_MUST_HAVE_AT_LEAST_6_CHARACTERS.name(), serviceResult.getMessage());
        assertNull(serviceResult.getObject());
    }

    @Test
    public void testRegisterWhenThereIsAnExistentEmail() throws Exception {
        final CompanyUserRepository companyUserRepository = createCompanyUserRepositoryForRegisterWhenThereIsAnExistentEmail();
        CompanyService companyService = createCompanyService(null, companyUserRepository, null, null, null);

        final CompanyRegistration companyRegistration = new CompanyRegistration();
        companyRegistration.setPassword("123456");
        companyRegistration.setPasswordConfirmation("123456");
        ServiceResult serviceResult = companyService.register(companyRegistration);
        assertNotNull(serviceResult);
        assertFalse(serviceResult.isSuccess());
        assertEquals(Translations.Message.EMAIL_ALREADY_EXISTS.name(), serviceResult.getMessage());
        assertNull(serviceResult.getObject());
        verify(companyUserRepository);
    }

    @Test
    public void testGetPointsInCompanyByPhone() throws Exception {
        final List<PointsInCompany> expectedPointsInCompanies = new ArrayList<>();
        expectedPointsInCompanies.add(createCompany(1, "name1", "logo1", 100));
        expectedPointsInCompanies.add(createCompany(2, "name2", "logo2", 200));
        final CompanyRepository companyRepository = createCompanyRepositoryForGetPoints(expectedPointsInCompanies);
        PointsConfigurationRepository pointsConfigurationRepository = createMock(PointsConfigurationRepository.class);
        ClientRepository clientRepository = createClientRepository();
        CompanyService companyService = createCompanyService(companyRepository, null, pointsConfigurationRepository, null, clientRepository);

        ServiceResult<List<PointsInCompany>> serviceResult = companyService.getPointsInCompanyByPhone("1234567890");
        assertNotNull(serviceResult);
        assertTrue(serviceResult.isSuccess());
        assertEquals("companies", serviceResult.getMessage());
        assertNotNull(serviceResult.getObject());

        List<PointsInCompany> actualClients = serviceResult.getObject();
        assertEquals(2, actualClients.size());
        assertEquals(1, actualClients.get(0).getCompanyId());
        assertEquals("name1", actualClients.get(0).getName());
        assertEquals("logo1", actualClients.get(0).getUrlImageLogo());
        assertEquals(100, actualClients.get(0).getPoints(), 0.00);
        assertEquals(2, actualClients.get(1).getCompanyId());
        assertEquals("name2", actualClients.get(1).getName());
        assertEquals("logo2", actualClients.get(1).getUrlImageLogo());
        assertEquals(200, actualClients.get(1).getPoints(), 0.00);
        verify(companyRepository, clientRepository);
    }

    @Test
    public void testUpdateImageLogo() throws Exception {
        CompanyRepository companyRepository = createCompanyRepositoryForUpdate();
        ThreadContext threadContext = new ThreadContext();
        threadContext.setProdEnvironment(false);
        ThreadContextService threadContextService = createThreadContextService(threadContext);
        CompanyService companyService = createCompanyService(companyRepository, null, null, threadContextService, null);
        List<FileItem> fileItems = new ArrayList<>();
        final FileItem fileItem = createFileItem();
        fileItems.add(fileItem);
        ServiceResult<Boolean> serviceResult = companyService.updateLogo(fileItems, 1);
        assertNotNull(serviceResult);
        assertTrue(serviceResult.isSuccess());
        assertEquals(Translations.Message.YOUR_LOGO_WAS_UPDATED.name(), serviceResult.getMessage());
        verify(companyRepository, fileItem);
    }

    @Test
    public void testGetByCompanyId() throws Exception {
        final Company company = new Company();
        company.setName("name");
        company.setUrlImageLogo("logo.png");
        CompanyRepository companyRepository = createCompanyRepositoryForGet(company);
        CompanyService companyService = createCompanyService(companyRepository, null, null, null, null);
        ServiceResult<Company> serviceResult = companyService.getByCompanyId(1);
        assertNotNull(serviceResult);
        assertTrue(serviceResult.isSuccess());
        assertEquals("name", company.getName());
        assertTrue(company.getUrlImageLogo().contains("logo.png"));
        verify(companyRepository);
    }

    @Test
    public void testGetLogo() throws Exception {
        final Company company = new Company();
        company.setName("name");
        company.setUrlImageLogo("logo.png");
        CompanyRepository companyRepository = createCompanyRepositoryForGet(company);
        CompanyService companyService = createCompanyService(companyRepository, null, null, createThreadContextService(new ThreadContext()), null);
        final File logo = companyService.getLogo(1);
        assertNotNull(logo);
    }

    @Test
    public void testSendMobileAppAdMessage() throws Exception {
        SMSService smsService = createStrictMock(SMSService.class);
        smsService.sendSMSMessage(anyString(), anyString());
        expectLastCall();

        CompanyRepository companyRepository = createNiceMock(CompanyRepository.class);
        expect(companyRepository.getByCompanyId(anyLong())).andReturn(Optional.of(new Company()));

        CompanyClientMappingRepository companyClientMappingRepository = createNiceMock(CompanyClientMappingRepository.class);
        expect(companyClientMappingRepository.getByCompanyIdClientId(anyLong(), anyLong())).andReturn(new CompanyClientMapping());

        ClientRepository clientRepository = createStrictMock(ClientRepository.class);
        expect(clientRepository.getByPhone(anyString())).andReturn(new Client());
        expect(clientRepository.updateCanReceivePromoSms(anyLong(), anyBoolean())).andReturn(1);

        replay(smsService, companyRepository, clientRepository, companyClientMappingRepository);

        CompanyService companyService =
            new CompanyService(companyRepository, null, null, clientRepository, null, null, smsService, companyClientMappingRepository) {
                @Override
                String getTranslation(Translations.Message message) {
                    return message.name();
                }

                @Override
                protected boolean isProdEnvironment() {
                    return true;
                }
            };
        final ServiceResult serviceResult = companyService.sendMobileAppAdMessage(0, "6623471507");
        assertTrue(serviceResult.isSuccess());
        assertEquals(Translations.Message.MOBILE_APP_AD_MESSAGE_SENT_SUCCESSFULLY.name(), serviceResult.getMessage());
        verify(smsService, clientRepository);
    }

    @Ignore
    public void testSendMobileAppAdMessageWhenIsNotProdEnv() throws Exception {
        CompanyService companyService =
            new CompanyService(null, null, null, null, null, null,  null, null) {
                @Override
                String getTranslation(Translations.Message message) {
                    return message.name();
                }

                @Override
                protected boolean isProdEnvironment() {
                    return false;
                }
            };
        final ServiceResult serviceResult = companyService.sendMobileAppAdMessage(0, "6623471507");
        assertFalse(serviceResult.isSuccess());
        assertEquals(Translations.Message.COMMON_USER_ERROR.name(), serviceResult.getMessage());
    }

    @Test
    public void testGetSMSMessage() {
        CompanyService companyService = new CompanyService(null, null, null, null, null, null, null, null) {
            @Override
            String getTranslation(Translations.Message message) {
                return "You've got %s points at %s. Install Neerpoints to see our promotions. %s";
            }
        };
        String smsMessage = companyService.getSMSMessage("New Company From an Awesome Place and a Big Name", 1000);
        assertNotNull(smsMessage);
        assertEquals("You've got 1000 points at New Company From an Awesome Place and a Big Name. Install Neerpoints to see our promotions. " +
            "http://tinyurl.com/og2b56y", smsMessage);

        companyService = new CompanyService(null, null, null, null, null, null, null, null) {
            @Override
            String getTranslation(Translations.Message message) {
                return "You've got %s points at %s. Install Neerpoints to see our promotions. %s";
            }
        };
        smsMessage = companyService.getSMSMessage("TG", 1000);
        assertNotNull(smsMessage);
        assertEquals("You've got 1000 points at TG. Install Neerpoints to see our promotions. " + "http://tinyurl.com/og2b56y", smsMessage);

        companyService = new CompanyService(null, null, null, null, null, null, null, null) {
            @Override
            String getTranslation(Translations.Message message) {
                return "You've got %s points at %s. Install Neerpoints to see our promotions. %s";
            }
        };
        smsMessage = companyService.getSMSMessage("New Company From an Awesome Place and a Big Name that does not fit in the message", 1000);
        assertNotNull(smsMessage);
        assertEquals("You've got 1000 points at New Company From an Awesome Place and a Big Name that does .... " +
            "Install Neerpoints to see our promotions. http://tinyurl.com/og2b56y", smsMessage);
    }

    @Test
    public void testGetSMSMessageWithInvalidTranslation() {
        CompanyService companyService = new CompanyService(null, null, null, null, null, null, null, null) {
            @Override
            String getTranslation(Translations.Message message) {
                return "You've got %s points at %s. Install Neerpoints to see our promotions and much much much much much much much much much " +
                    "much much much much much much more. %s";
            }
        };
        try {
            companyService.getSMSMessage("New Company From an Awesome Place and a Big Name that does not fit in the message", 1000);
        } catch (IllegalArgumentException e) {
            assertEquals(
                "Message length must be less than 160 in: You've got %s points at %s. Install Neerpoints to see our promotions and much much " +
                    "much much much much much much much much much much much much much more. %s", e.getMessage());
        }

        companyService = new CompanyService(null, null, null, null, null, null, null, null) {
            @Override
            String getTranslation(Translations.Message message) {
                return "You've got %s points at %s. Install Neerpoints to see our promotions and much much much much much much much much much much " +
                    "much much much much much much much much much much much much much much much much much much much much more. %s";
            }
        };
        try {
            companyService.getSMSMessage("TG", 1000);
        } catch (IllegalArgumentException e) {
            assertEquals("Message length must be less than 160 in: You've got %s points at %s. Install Neerpoints to see our promotions and much " +
                "much much much much much much much much much much much much much much much much much much much much much much much much much much " +
                "much much much more. %s", e.getMessage());
        }
    }

    private CompanyService createCompanyService(final CompanyRepository companyRepository, final CompanyUserRepository companyUserRepository,
        final PointsConfigurationRepository pointsConfigurationRepository, final ThreadContextService threadContextService,
        ClientRepository clientRepository) {
        return new CompanyService(companyRepository, companyUserRepository, pointsConfigurationRepository, clientRepository, threadContextService,
            null, null, null) {
            @Override
            void sendActivationEmail(String email, String activationKey) throws MessagingException {

            }

            @Override
            String getTranslation(Translations.Message message) {
                return message.name();
            }
        };
    }

    private FileItem createFileItem() throws Exception {
        FileItem fileItem = createMock(FileItem.class);
        expect(fileItem.getContentType()).andReturn("image/png");
        fileItem.write((File) anyObject());
        expectLastCall();
        replay(fileItem);
        return fileItem;
    }

    private ClientRepository createClientRepository() throws Exception {
        ClientRepository clientRepository = createMock(ClientRepository.class);
        expect(clientRepository.getByPhone(anyString())).andReturn(new Client());
        replay(clientRepository);
        return clientRepository;
    }

    private ThreadContextService createThreadContextService(QueryAgent queryAgent) throws SQLException {
        ThreadContextService threadContextService = createMock(ThreadContextService.class);
        expect(threadContextService.getQueryAgent()).andReturn(queryAgent).times(2);
        replay(threadContextService);
        return threadContextService;
    }

    private ThreadContextService createThreadContextService(ThreadContext threadContext) throws SQLException {
        ThreadContextService threadContextService = createMock(ThreadContextService.class);
        expect(threadContextService.getThreadContext()).andReturn(threadContext).times(1);
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

    private PointsInCompany createCompany(long companyId, String name, String urlImageLogo, float points) {
        PointsInCompany pointsInCompany = new PointsInCompany();
        pointsInCompany.setCompanyId(companyId);
        pointsInCompany.setName(name);
        pointsInCompany.setUrlImageLogo(urlImageLogo);
        pointsInCompany.setPoints(points);
        return pointsInCompany;
    }

    private CompanyUserRepository createCompanyUserRepository() throws Exception {
        final CompanyUserRepository companyUserRepository = EasyMock.createMock(CompanyUserRepository.class);
        expect(companyUserRepository.insert((CompanyUser) anyObject())).andReturn(1l).times(1);
        expect(companyUserRepository.getByEmail(anyString())).andReturn(null).times(1);
        replay(companyUserRepository);
        return companyUserRepository;
    }

    private CompanyRepository createCompanyRepository() throws Exception {
        final CompanyRepository companyRepository = EasyMock.createMock(CompanyRepository.class);
        expect(companyRepository.insert((Company) anyObject())).andReturn(1l).times(1);
        replay(companyRepository);
        return companyRepository;
    }

    private CompanyRepository createCompanyRepositoryForUpdate() throws Exception {
        final CompanyRepository companyRepository = EasyMock.createMock(CompanyRepository.class);
        expect(companyRepository.updateUrlImageLogo(anyLong(), anyString())).andReturn(1).times(1);
        replay(companyRepository);
        return companyRepository;
    }

    private CompanyRepository createCompanyRepositoryForGetPoints(List<PointsInCompany> pointsInCompanies) throws Exception {
        CompanyRepository clientRepository = createMock(CompanyRepository.class);
        expect(clientRepository.getPointsInCompanyByClientId(anyLong())).andReturn(pointsInCompanies).anyTimes();
        replay(clientRepository);
        return clientRepository;
    }

    private CompanyRepository createCompanyRepositoryForGet(Company company) throws Exception {
        CompanyRepository clientRepository = createMock(CompanyRepository.class);
        expect(clientRepository.getByCompanyId(anyLong())).andReturn(Optional.ofNullable(company)).anyTimes();
        replay(clientRepository);
        return clientRepository;
    }

    private PointsConfigurationRepository createPointsConfigurationRepository() throws Exception {
        PointsConfigurationRepository pointsConfigurationRepository = createMock(PointsConfigurationRepository.class);
        expect(pointsConfigurationRepository.insert((PointsConfiguration) anyObject())).andReturn(1l);
        replay(pointsConfigurationRepository);
        return pointsConfigurationRepository;
    }

    private CompanyUserRepository createCompanyUserRepositoryForRegisterWhenThereIsAnExistentEmail() throws Exception {
        final CompanyUserRepository companyUserRepository = EasyMock.createMock(CompanyUserRepository.class);
        expect(companyUserRepository.getByEmail(anyString())).andReturn(new CompanyUser()).times(1);
        replay(companyUserRepository);
        return companyUserRepository;
    }
}