package com.lealpoints.service.implementations;

import com.lealpoints.context.ThreadContext;
import com.lealpoints.context.ThreadContextService;
import com.lealpoints.db.queryagent.QueryAgent;
import com.lealpoints.environments.DevEnvironment;
import com.lealpoints.i18n.Message;
import com.lealpoints.model.*;
import com.lealpoints.repository.*;
import com.lealpoints.service.NotificationService;
import com.lealpoints.service.model.CompanyRegistration;
import com.lealpoints.service.response.ServiceMessage;
import com.lealpoints.service.response.ServiceResult;
import com.lealpoints.service.util.ServiceUtil;
import org.apache.commons.fileupload.FileItem;
import org.easymock.EasyMock;
import org.junit.Test;

import javax.mail.MessagingException;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class CompanyServiceImplTest extends BaseServiceTest {
    @Test
    public void testRegister() throws Exception {
        final CompanyRepository companyRepository = createCompanyRepository();
        final CompanyUserRepository companyUserRepository = createCompanyUserRepository();
        final PointsConfigurationRepository pointsConfigurationRepository = createPointsConfigurationRepository();
        final QueryAgent queryAgent = createQueryAgent();
        ThreadContext threadContext = new ThreadContext();
        threadContext.setEnvironment(new DevEnvironment());
        final ThreadContextService threadContextService = createThreadContextServiceForRegistering(queryAgent, threadContext);
        PromotionConfigurationRepository promotionConfigurationRepository = createStrictMock(PromotionConfigurationRepository.class);
        expect(promotionConfigurationRepository.insert(EasyMock.<PromotionConfiguration>anyObject())).andReturn(1L);
        replay(promotionConfigurationRepository);
        NotificationService notificationService = createNotificationService();
        CompanyServiceImpl companyService =
                createCompanyService(companyRepository, companyUserRepository, pointsConfigurationRepository, threadContextService, null,
                        promotionConfigurationRepository, notificationService);
        final CompanyRegistration companyRegistration = new CompanyRegistration();
        companyRegistration.setCompanyName("company name");
        companyRegistration.setUsername("user name");
        companyRegistration.setEmail("email@test.com");
        companyRegistration.setPassword("Pa$$w0rd");
        companyRegistration.setPasswordConfirmation("Pa$$w0rd");
        companyRegistration.setUrlImageLogo("images/logo.png");

        ServiceResult serviceResult = companyService.register(companyRegistration);
        assertNotNull(serviceResult);
        assertTrue(serviceResult.isSuccess());
        assertNotNull(serviceResult.getMessage());
        assertEquals(Message.WE_HAVE_SENT_YOU_AND_ACTIVATION_LINK.name(), serviceResult.getMessage());
        verify(companyRepository, companyUserRepository, threadContextService, queryAgent);
    }

    private NotificationService createNotificationService() throws MessagingException {
        NotificationService notificationService = createMock(NotificationService.class);
        notificationService.sendActivationEmail(anyString(), anyString());
        expectLastCall();
        replay(notificationService);
        return notificationService;
    }

    @Test
    public void testRegisterWhenPasswordIsDifferentFromConfirmation() throws Exception {
        final CompanyServiceImpl companyService = createCompanyService(null, null, null, null, null, null, null);
        final CompanyRegistration companyRegistration = new CompanyRegistration();
        companyRegistration.setPassword("123456");
        companyRegistration.setPasswordConfirmation("123457");
        companyRegistration.setCompanyName("company");
        ServiceResult serviceResult = companyService.register(companyRegistration);
        assertNotNull(serviceResult);
        assertFalse(serviceResult.isSuccess());
        assertEquals(Message.PASSWORD_AND_CONFIRMATION_ARE_DIFFERENT.name(), serviceResult.getMessage());
        assertNull(serviceResult.getObject());
    }

    @Test
    public void testRegisterWhenPasswordIsShort() throws Exception {
        CompanyServiceImpl companyService = createCompanyService(null, null, null, null, null, null, null);
        final CompanyRegistration companyRegistration = new CompanyRegistration();
        companyRegistration.setPassword("12345");
        companyRegistration.setPasswordConfirmation("12345");
        companyRegistration.setCompanyName("company");
        ServiceResult serviceResult = companyService.register(companyRegistration);
        assertNotNull(serviceResult);
        assertFalse(serviceResult.isSuccess());
        assertEquals(Message.PASSWORD_MUST_HAVE_AT_LEAST_6_CHARACTERS.name(), serviceResult.getMessage());
        assertNull(serviceResult.getObject());
    }

    @Test
    public void testRegisterWhenThereIsAnExistentEmail() throws Exception {
        final CompanyUserRepository companyUserRepository = createCompanyUserRepositoryForRegisterWhenThereIsAnExistentEmail();
        CompanyServiceImpl companyService = createCompanyService(null, companyUserRepository, null, null, null, null, null);

        final CompanyRegistration companyRegistration = new CompanyRegistration();
        companyRegistration.setPassword("123456");
        companyRegistration.setPasswordConfirmation("123456");
        companyRegistration.setEmail("test@lealpoints.com");
        companyRegistration.setCompanyName("company");
        ServiceResult serviceResult = companyService.register(companyRegistration);
        assertNotNull(serviceResult);
        assertFalse(serviceResult.isSuccess());
        assertEquals(Message.EMAIL_ALREADY_EXISTS.name(), serviceResult.getMessage());
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
        CompanyServiceImpl companyService =
                createCompanyService(companyRepository, null, pointsConfigurationRepository, null, clientRepository, null, null);

        ServiceResult<List<PointsInCompany>> serviceResult = companyService.getPointsInCompanyByPhone("1234567890");
        assertNotNull(serviceResult);
        assertTrue(serviceResult.isSuccess());
        assertEquals("", serviceResult.getMessage());
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
        threadContext.setEnvironment(new DevEnvironment());
        ThreadContextService threadContextService = createThreadContextService(threadContext);
        CompanyServiceImpl companyService = createCompanyService(companyRepository, null, null, threadContextService,
                null, null, null);
        List<FileItem> fileItems = new ArrayList<>();
        final FileItem fileItem = createFileItem();
        fileItems.add(fileItem);
        ServiceResult<Boolean> serviceResult = companyService.updateLogo(fileItems, 1);
        assertNotNull(serviceResult);
        assertTrue(serviceResult.isSuccess());
        assertEquals(Message.YOUR_LOGO_WAS_UPDATED.name(), serviceResult.getMessage());
        verify(companyRepository, fileItem);
    }

    @Test
    public void testGetByCompanyId() throws Exception {
        final Company company = new Company();
        company.setName("name");
        company.setUrlImageLogo("logo.png");
        CompanyRepository companyRepository = createCompanyRepositoryForGet(company);
        CompanyServiceImpl companyService = createCompanyService(companyRepository, null, null, null, null, null, null);
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
        ThreadContext threadContext = createMock(ThreadContext.class);
        expect(threadContext.getEnvironment()).andReturn(new DevEnvironment());
        replay(threadContext);
        CompanyServiceImpl companyService =
                createCompanyService(companyRepository, null, null, createThreadContextService(threadContext), null, null, null);
        final File logo = companyService.getLogo(1);
        assertNotNull(logo);
    }

    @Test
    public void testGetLogoWithoutUrlImage() throws Exception {
        final Company company = new Company();
        company.setName("name");
        company.setUrlImageLogo("");
        CompanyRepository companyRepository = createCompanyRepositoryForGet(company);
        ThreadContext threadContext = createMock(ThreadContext.class);
        expect(threadContext.getEnvironment()).andReturn(new DevEnvironment());
        replay(threadContext);
        CompanyServiceImpl companyService =
                createCompanyService(companyRepository, null, null, createThreadContextService(threadContext), null, null, null);
        final File logo = companyService.getLogo(1);
        assertNotNull(logo);
    }

    private CompanyServiceImpl createCompanyService(final CompanyRepository companyRepository,
                                                    final CompanyUserRepository companyUserRepository,
                                                    final PointsConfigurationRepository pointsConfigurationRepository,
                                                    final ThreadContextService threadContextService,
                                                    ClientRepository clientRepository,
                                                    PromotionConfigurationRepository promotionConfigurationRepository,
                                                    NotificationService notificationService) {
        return new CompanyServiceImpl(companyRepository, companyUserRepository, pointsConfigurationRepository, clientRepository, threadContextService,
                promotionConfigurationRepository, new ServiceUtil(), notificationService) {

            @Override
            public ServiceMessage getServiceMessage(Message message, String... params) {
                return new ServiceMessage(message.name());
            }

            @Override void registerPointsConfiguration(long companyId) throws Exception
            {
                //Do nothing
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

    private ThreadContextService createThreadContextServiceForRegistering(QueryAgent queryAgent,
                                                                          ThreadContext threadContext) throws SQLException {
        ThreadContextService threadContextService = createMock(ThreadContextService.class);
        expect(threadContextService.getQueryAgent()).andReturn(queryAgent).times(2);
        expect(threadContextService.getThreadContext()).andReturn(threadContext).times(2);
        replay(threadContextService);
        return threadContextService;
    }

    private ThreadContextService createThreadContextService(ThreadContext threadContext) throws SQLException {
        ThreadContextService threadContextService = createMock(ThreadContextService.class);
        expect(threadContextService.getThreadContext()).andReturn(threadContext).times(1);
        replay(threadContextService);
        return threadContextService;
    }

    private CompanyUserRepository createCompanyUserRepository() throws Exception {
        final CompanyUserRepository companyUserRepository = EasyMock.createMock(CompanyUserRepository.class);
        expect(companyUserRepository.insert((CompanyUser) anyObject())).andReturn(1L).times(1);
        expect(companyUserRepository.getByEmail(anyString())).andReturn(null).times(1);
        replay(companyUserRepository);
        return companyUserRepository;
    }

    private CompanyRepository createCompanyRepository() throws Exception {
        final CompanyRepository companyRepository = EasyMock.createMock(CompanyRepository.class);
        expect(companyRepository.insert((Company) anyObject())).andReturn(1L).times(1);
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
        expect(clientRepository.getByCompanyId(anyLong())).andReturn(company).anyTimes();
        replay(clientRepository);
        return clientRepository;
    }

    private PointsConfigurationRepository createPointsConfigurationRepository() throws Exception {
        PointsConfigurationRepository pointsConfigurationRepository = createMock(PointsConfigurationRepository.class);
        expect(pointsConfigurationRepository.insert((PointsConfiguration) anyObject())).andReturn(1L);
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
