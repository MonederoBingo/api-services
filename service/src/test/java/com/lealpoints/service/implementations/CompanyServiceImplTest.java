package com.lealpoints.service.implementations;

import com.lealpoints.i18n.Message;
import com.lealpoints.model.*;
import com.lealpoints.repository.ClientRepository;
import com.lealpoints.repository.CompanyRepository;
import com.lealpoints.repository.CompanyUserRepository;
import com.lealpoints.repository.PromotionConfigurationRepository;
import com.lealpoints.service.NotificationService;
import com.lealpoints.service.PointsConfigurationService;
import com.lealpoints.service.model.CompanyRegistration;
import com.lealpoints.service.response.ServiceMessage;
import com.lealpoints.service.response.ServiceResult;
import com.lealpoints.service.util.ServiceUtil;
import org.apache.commons.fileupload.FileItem;
import org.easymock.EasyMock;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import xyz.greatapp.libs.service.Environment;
import xyz.greatapp.libs.service.context.ThreadContext;
import xyz.greatapp.libs.service.context.ThreadContextService;

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
        ThreadContext threadContext = new ThreadContext();
        threadContext.setEnvironment(Environment.DEV);
        final ThreadContextService threadContextService = createThreadContextServiceForRegistering(threadContext);
        PromotionConfigurationRepository promotionConfigurationRepository = createStrictMock(PromotionConfigurationRepository.class);
        expect(promotionConfigurationRepository.insert(EasyMock.<PromotionConfiguration>anyObject())).andReturn(1L);
        replay(promotionConfigurationRepository);
        NotificationService notificationService = createNotificationService();
        PointsConfigurationService pointsConfigurationService = createMock(PointsConfigurationService.class);
        CompanyServiceImpl companyService = createCompanyService(companyRepository, companyUserRepository, threadContextService, null,
                promotionConfigurationRepository, notificationService, pointsConfigurationService);
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
        verify(companyRepository, companyUserRepository, threadContextService);
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
        //given
        final List<PointsInCompany> expectedPointsInCompanies = new ArrayList<>();
        expectedPointsInCompanies.add(createCompany(1, "name1", "logo1", 100));
        expectedPointsInCompanies.add(createCompany(2, "name2", "logo2", 200));
        final CompanyRepository companyRepository = createCompanyRepositoryForGetPoints(expectedPointsInCompanies);
        ClientRepository clientRepository = createClientRepository();
        CompanyServiceImpl companyService = createCompanyService(companyRepository, null, null, clientRepository, null, null, null);

        //when
        xyz.greatapp.libs.service.ServiceResult serviceResult = companyService.getPointsInCompanyByPhone("1234567890");

        //then
        assertNotNull(serviceResult);
        assertTrue(serviceResult.isSuccess());
        assertEquals("", serviceResult.getMessage());
        assertNotNull(serviceResult.getObject());

        JSONArray actualClients = new JSONArray(serviceResult.getObject());
        assertEquals(2, actualClients.length());

        JSONObject client1 = actualClients.getJSONObject(0);
        assertEquals(1, client1.get("company_id"));
        assertEquals("name1", client1.get("name"));
        assertEquals("logo1", client1.get("url_image_logo"));
        assertEquals(100, client1.getDouble("points"), 0.00);

        JSONObject client2 = actualClients.getJSONObject(1);
        assertEquals(2, client2.get("company_id"));
        assertEquals("name2", client2.get("name"));
        assertEquals("logo2", client2.get("url_image_logo"));
        assertEquals(200, client2.getDouble("points"), 0.00);
        verify(companyRepository, clientRepository);
    }

    @Test
    public void testUpdateImageLogo() throws Exception {
        CompanyRepository companyRepository = createCompanyRepositoryForUpdate();
        ThreadContext threadContext = new ThreadContext();
        threadContext.setEnvironment(Environment.DEV);
        ThreadContextService threadContextService = createThreadContextService(threadContext);
        CompanyServiceImpl companyService = createCompanyService(companyRepository, null, threadContextService,
                null, null, null, null);
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
        //given
        final Company company = new Company();
        company.setName("name");
        company.setUrlImageLogo("logo.png");
        CompanyRepository companyRepository = createCompanyRepositoryForGet(company);
        CompanyServiceImpl companyService = createCompanyService(companyRepository, null, null, null, null, null, null);

        //when
        xyz.greatapp.libs.service.ServiceResult serviceResult = companyService.getByCompanyId(1);

        //then
        assertNotNull(serviceResult);
        assertTrue(serviceResult.isSuccess());
        assertEquals("name", new JSONObject(serviceResult.getObject()).get("name"));
        assertTrue(new JSONObject(serviceResult.getObject()).get("url_image_logo").toString().contains("logo.png"));
        verify(companyRepository);
    }

    @Test
    public void testGetLogo() throws Exception {
        //given
        final Company company = new Company();
        company.setName("name");
        company.setUrlImageLogo("logo.png");
        CompanyRepository companyRepository = createCompanyRepositoryForGet(company);
        ThreadContext threadContext = createMock(ThreadContext.class);
        expect(threadContext.getEnvironment()).andReturn(Environment.DEV);
        replay(threadContext);
        CompanyServiceImpl companyService = createCompanyService(
                companyRepository,
                null,
                createThreadContextService(threadContext),
                null,
                null,
                null,
                null);

        //when
        final File logo = companyService.getLogo(1);

        //then
        assertNotNull(logo);
    }

    @Test
    public void testGetLogoWithoutUrlImage() throws Exception {
        final Company company = new Company();
        company.setName("name");
        company.setUrlImageLogo("");
        CompanyRepository companyRepository = createCompanyRepositoryForGet(company);
        ThreadContext threadContext = createMock(ThreadContext.class);
        expect(threadContext.getEnvironment()).andReturn(Environment.DEV);
        replay(threadContext);
        CompanyServiceImpl companyService = createCompanyService(companyRepository, null, createThreadContextService(threadContext), null, null, null, null);
        final File logo = companyService.getLogo(1);
        assertNotNull(logo);
    }

    private CompanyServiceImpl createCompanyService(final CompanyRepository companyRepository,
                                                    final CompanyUserRepository companyUserRepository,
                                                    final ThreadContextService threadContextService,
                                                    ClientRepository clientRepository,
                                                    PromotionConfigurationRepository promotionConfigurationRepository,
                                                    NotificationService notificationService, PointsConfigurationService pointsConfigurationService) {
        return new CompanyServiceImpl(companyRepository, companyUserRepository, clientRepository, threadContextService,
                promotionConfigurationRepository, new ServiceUtil(), notificationService, pointsConfigurationService) {

            @Override
            public ServiceMessage getServiceMessage(Message message, String... params) {
                return new ServiceMessage(message.name());
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
        expect(clientRepository.getByPhone(anyString())).andReturn(new xyz.greatapp.libs.service.ServiceResult(true, "", new Client().toJSONObject().toString()));
        replay(clientRepository);
        return clientRepository;
    }

    private ThreadContextService createThreadContextServiceForRegistering(ThreadContext threadContext) throws SQLException {
        ThreadContextService threadContextService = createMock(ThreadContextService.class);
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
        expect(companyUserRepository.getByEmail(anyString()))
                .andReturn(new xyz.greatapp.libs.service.ServiceResult(true, "", "{}"))
                .times(1);
        expect(companyUserRepository.insert((CompanyUser) anyObject())).andReturn(1L).times(1);
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
        JSONObject[] strings = new JSONObject[pointsInCompanies.size()];
        for (int i = 0; i < pointsInCompanies.size(); i++) {
            strings[i] = pointsInCompanies.get(i).toJSONObject();
        }
        expect(clientRepository.getPointsInCompanyByClientId(anyLong()))
                .andReturn(new xyz.greatapp.libs.service.ServiceResult(true, "", new JSONArray(strings).toString()))
                .anyTimes();
        replay(clientRepository);
        return clientRepository;
    }

    private CompanyRepository createCompanyRepositoryForGet(Company company) throws Exception {
        CompanyRepository clientRepository = createMock(CompanyRepository.class);
        expect(clientRepository.getByCompanyId(anyLong()))
                .andReturn(new xyz.greatapp.libs.service.ServiceResult(true, "", company.toJSONObject().toString()))
                .anyTimes();
        replay(clientRepository);
        return clientRepository;
    }

    private CompanyUserRepository createCompanyUserRepositoryForRegisterWhenThereIsAnExistentEmail() throws Exception {
        final CompanyUserRepository companyUserRepository = EasyMock.createMock(CompanyUserRepository.class);
        expect(companyUserRepository.getByEmail(anyString()))
                .andReturn(new xyz.greatapp.libs.service.ServiceResult(true, "", new CompanyUser().toJSONObject().toString()))
                .times(1);
        replay(companyUserRepository);
        return companyUserRepository;
    }
}
