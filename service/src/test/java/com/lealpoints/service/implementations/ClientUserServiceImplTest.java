package com.lealpoints.service.implementations;

import com.lealpoints.context.ThreadContext;
import com.lealpoints.context.ThreadContextService;
import com.lealpoints.db.queryagent.QueryAgent;
import com.lealpoints.environments.DevEnvironment;
import com.lealpoints.i18n.Message;
import com.lealpoints.model.Client;
import com.lealpoints.model.ClientUser;
import com.lealpoints.repository.ClientRepository;
import com.lealpoints.repository.ClientUserRepository;
import com.lealpoints.service.model.ClientLoginResult;
import com.lealpoints.service.model.ClientUserLogin;
import com.lealpoints.service.model.ClientUserRegistration;
import com.lealpoints.service.response.ServiceMessage;
import com.lealpoints.service.response.ServiceResult;
import org.easymock.EasyMock;
import org.junit.Test;

import javax.mail.MessagingException;
import java.sql.SQLException;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class ClientUserServiceImplTest extends BaseServiceTest {

    @Test
    public void testRegister() throws Exception {
        ClientUserRepository clientUserRepository = createClientUserRepository();
        ClientRepository clientRepository = createClientRepositoryForRegister();
        final QueryAgent queryAgent = createQueryAgent();

        ThreadContext threadContext = new ThreadContext();
        threadContext.setEnvironment(new DevEnvironment());
        final ThreadContextService threadContextService = createThreadContextService(queryAgent, threadContext);
        ClientUserServiceImpl clientUserService =
                new ClientUserServiceImpl(clientUserRepository, clientRepository, threadContextService, null) {
                    @Override
                    public ServiceMessage getServiceMessage(Message message, String... params) {
                        return new ServiceMessage(message.name());
                    }

                    @Override
                    String generateAndSendRegistrationSMS(String phone) throws MessagingException {
                        return "123456";
                    }
                };
        ClientUserRegistration clientUserRegistration = new ClientUserRegistration();
        clientUserRegistration.setPhoneNumber("1234567890");
        ServiceResult<String> serviceResult = clientUserService.register(clientUserRegistration);
        assertNotNull(serviceResult);
        assertEquals("123456", serviceResult.getObject());
        verify(clientRepository, clientRepository);
    }

    @Test
    public void testRegisterWhenClientExists() throws Exception {
        //given
        final ClientUser clientUser = new ClientUser();
        clientUser.setClientUserId(2);
        ClientUserRepository clientUserRepository = createClientUserRepositoryWhenClientExists(clientUser);
        ClientRepository clientRepository = createClientRepositoryForRegister();
        final QueryAgent queryAgent = createQueryAgent();
        ThreadContext threadContext = new ThreadContext();
        threadContext.setEnvironment(new DevEnvironment());
        final ThreadContextService threadContextService = createThreadContextService(queryAgent, threadContext);
        ClientUserServiceImpl clientUserService =
                new ClientUserServiceImpl(clientUserRepository, clientRepository, threadContextService, null) {
                    @Override
                    String generateAndSendRegistrationSMS(String phone) throws MessagingException {
                        return "123456";
                    }
                };
        ClientUserRegistration clientUserRegistration = new ClientUserRegistration();
        clientUserRegistration.setPhoneNumber("1234567890");

        //when
        ServiceResult<String> serviceResult = clientUserService.register(new ClientUserRegistration());

        //then
        assertNotNull(serviceResult);
        assertEquals("123456", serviceResult.getObject());
        verify(clientRepository, clientRepository);
    }

    @Test
    public void testRegisterWithInvalidPhone() {
        final ClientUserServiceImpl clientUserService = new ClientUserServiceImpl(null, null, null, null) {
            @Override
            public ServiceMessage getServiceMessage(Message message, String... params) {
                return new ServiceMessage(message.name());
            }

            @Override
            String generateAndSendRegistrationSMS(String phone) throws MessagingException {
                return "123456";
            }
        };
        ClientUserRegistration clientUserRegistration = new ClientUserRegistration();
        clientUserRegistration.setPhoneNumber("123");
        ServiceResult<String> serviceResult = clientUserService.register(clientUserRegistration);
        assertNotNull(serviceResult);
        assertEquals(Message.PHONE_MUST_HAVE_10_DIGITS.name(), serviceResult.getMessage());
    }

    @Test
    public void testUserLoginWithPhone() throws Exception {
        ClientUser clientUser = new ClientUser();
        clientUser.setClientUserId(1);
        clientUser.setClientId(1);
        clientUser.setName("name");
        clientUser.setEmail("a@a.com");
        clientUser.setPassword("password");
        clientUser.setSmsKey("qwerty");
        final ClientUserRepository clientUserRepository = createClientUserRepositoryForPhone(clientUser);
        final ClientRepository clientRepository = createClientRepository();
        final ClientUserServiceImpl clientUserService =
                new ClientUserServiceImpl(clientUserRepository, clientRepository, createMock(ThreadContextService.class), null) {
                    @Override
                    String generateAndSendRegistrationSMS(String phone) throws MessagingException {
                        return "";
                    }
                };

        ClientUserLogin clientUserLogin = new ClientUserLogin();
        clientUserLogin.setPhoneNumber("6141112233");
        clientUserLogin.setSmsKey("qwerty");
        clientUserLogin.setEmail("");
        clientUserLogin.setPassword("");
        ServiceResult<ClientLoginResult> serviceResult = clientUserService.login(clientUserLogin);
        assertNotNull(serviceResult);
        assertTrue(serviceResult.isSuccess());
        assertEquals(clientUser.getClientId(), serviceResult.getObject().getClientUserId());
        verify(clientUserRepository, clientRepository);
    }

    @Test
    public void testUserLoginWithPhoneWhenNotUpdatingApiKey() throws Exception {
        ClientUser clientUser = new ClientUser();
        clientUser.setClientUserId(1);
        clientUser.setClientId(1);
        clientUser.setName("name");
        clientUser.setEmail("a@a.com");
        clientUser.setPassword("password");
        clientUser.setSmsKey("qwerty");
        final ClientUserRepository clientUserRepository = createClientUserRepositoryForPhoneWhenNotUpdatingApiKey(clientUser);
        final ClientRepository clientRepository = createClientRepository();
        final ClientUserServiceImpl clientUserService =
                new ClientUserServiceImpl(clientUserRepository, clientRepository, createMock(ThreadContextService.class), null) {
                    @Override
                    String generateAndSendRegistrationSMS(String phone) throws MessagingException {
                        return "";
                    }

                    @Override
                    public ServiceMessage getServiceMessage(Message message, String... params) {
                        return new ServiceMessage(message.name());
                    }
                };

        ClientUserLogin clientUserLogin = new ClientUserLogin();
        clientUserLogin.setPhoneNumber("6141112233");
        clientUserLogin.setSmsKey("qwerty");
        clientUserLogin.setEmail("");
        clientUserLogin.setPassword("");
        ServiceResult serviceResult = clientUserService.login(clientUserLogin);
        assertNotNull(serviceResult);
        assertFalse(serviceResult.isSuccess());
        assertEquals(Message.COMMON_USER_ERROR.name(), serviceResult.getMessage());
        verify(clientUserRepository, clientRepository);
    }

    @Test
    public void testUserLoginWithEmail() throws Exception {
        ClientUser clientUser = new ClientUser();
        clientUser.setClientUserId(1);
        clientUser.setClientId(1);
        clientUser.setName("name");
        clientUser.setEmail("a@a.com");
        clientUser.setPassword("password");
        clientUser.setSmsKey("qwerty");
        final ClientUserRepository clientUserRepository = createClientUserRepositoryForEmail(clientUser);
        final ClientRepository clientRepository = createClientRepository();
        final ClientUserServiceImpl clientUserService =
                new ClientUserServiceImpl(clientUserRepository, clientRepository, createMock(ThreadContextService.class), null) {
                    @Override
                    String generateAndSendRegistrationSMS(String phone) throws MessagingException {
                        return "";
                    }
                };

        ClientUserLogin clientUserLogin = new ClientUserLogin();
        clientUserLogin.setPhoneNumber("");
        clientUserLogin.setSmsKey("");
        clientUserLogin.setEmail("a@a.com");
        clientUserLogin.setPassword("password");
        ServiceResult<ClientLoginResult> serviceResult = clientUserService.login(clientUserLogin);
        assertNotNull(serviceResult);
        assertTrue(serviceResult.isSuccess());
        assertEquals(clientUser.getClientId(), serviceResult.getObject().getClientUserId());
        verify(clientUserRepository, clientRepository);
    }

    @Test
    public void testUserLoginWithEmailWhenNotUpdatingApiKey() throws Exception {
        //given
        ClientUser clientUser = new ClientUser();
        clientUser.setClientUserId(1);
        clientUser.setClientId(1);
        clientUser.setName("name");
        clientUser.setEmail("a@a.com");
        clientUser.setPassword("password");
        clientUser.setSmsKey("qwerty");
        final ClientUserRepository clientUserRepository = createClientUserRepositoryForEmailWhenNotUpdatingApiKey(clientUser);
        final ClientRepository clientRepository = createClientRepository();
        final ClientUserServiceImpl clientUserService =
                new ClientUserServiceImpl(clientUserRepository, clientRepository, createMock(ThreadContextService.class), null) {
                    @Override
                    String generateAndSendRegistrationSMS(String phone) throws MessagingException {
                        return "";
                    }

                    @Override
                    public ServiceMessage getServiceMessage(Message message, String... params) {
                        return new ServiceMessage(message.name());
                    }
                };

        ClientUserLogin clientUserLogin = new ClientUserLogin();
        clientUserLogin.setPhoneNumber("");
        clientUserLogin.setSmsKey("");
        clientUserLogin.setEmail("a@a.com");
        clientUserLogin.setPassword("password");

        //when
        ServiceResult serviceResult = clientUserService.login(clientUserLogin);

        //then
        assertNotNull(serviceResult);
        assertFalse(serviceResult.isSuccess());
        assertEquals(Message.COMMON_USER_ERROR.name(), serviceResult.getMessage());
        verify(clientUserRepository, clientRepository);
    }

    @Test
    public void testResendKey() throws Exception {
        ThreadContext threadContext = new ThreadContext();
        threadContext.setEnvironment(new DevEnvironment());
        ThreadContextService threadContextService = createThreadContextService(threadContext, 1);
        ClientUserServiceImpl clientUserService =
                new ClientUserServiceImpl(createClientUserRepositoryForUpdateSms(), null, threadContextService, null) {
                    @Override
                    String generateAndSendRegistrationSMS(String phone) throws MessagingException {
                        return "";
                    }
                };
        ServiceResult<Boolean> serviceResult = clientUserService.resendKey("1234");
        assertNotNull(serviceResult);
        assertTrue(serviceResult.isSuccess());
        assertEquals("", serviceResult.getMessage());
        assertTrue(serviceResult.getObject());
    }

    @Test
    public void testGenerateAndSendRegistrationSMS() throws Exception {
        ThreadContext threadContext = new ThreadContext();
        threadContext.setEnvironment(new DevEnvironment());
        ThreadContextService threadContextService = createThreadContextService(threadContext, 3);
        ClientUserServiceImpl clientUserService =
                new ClientUserServiceImpl(createClientUserRepositoryForUpdateSms(), null, threadContextService, null) {

                    @Override
                    public ServiceMessage getServiceMessage(Message message, String... params) {
                        return new ServiceMessage(message.name());
                    }
                };
        final String key = clientUserService.generateAndSendRegistrationSMS("1234567890");
        assertNotNull(key);
        assertNotEquals("", key);
    }

    private ThreadContextService createThreadContextService(QueryAgent queryAgent, ThreadContext threadContext) throws SQLException {
        ThreadContextService threadContextService = createMock(ThreadContextService.class);
        expect(threadContextService.getQueryAgent()).andReturn(queryAgent).times(2);
        expect(threadContextService.getThreadContext()).andReturn(threadContext).times(1);
        replay(threadContextService);
        return threadContextService;
    }

    private ThreadContextService createThreadContextService(ThreadContext threadContext, int times) throws SQLException {
        ThreadContextService threadContextService = createMock(ThreadContextService.class);
        expect(threadContextService.getThreadContext()).andReturn(threadContext).times(times);
        replay(threadContextService);
        return threadContextService;
    }

    private ClientUserRepository createClientUserRepositoryForPhone(ClientUser clientUser) throws Exception {
        final ClientUserRepository clientUserRepository = EasyMock.createMock(ClientUserRepository.class);
        expect(clientUserRepository.getByPhoneAndKey(anyString(), anyString())).andReturn(
                new xyz.greatapp.libs.service.ServiceResult(true, "", clientUser.toJSONObject().toString()));
        expect(clientUserRepository.updateApiKeyById(anyLong(), anyString())).andReturn(1);
        replay(clientUserRepository);
        return clientUserRepository;
    }

    private ClientUserRepository createClientUserRepositoryForPhoneWhenNotUpdatingApiKey(ClientUser clientUser) throws Exception {
        final ClientUserRepository clientUserRepository = EasyMock.createMock(ClientUserRepository.class);
        expect(clientUserRepository.getByPhoneAndKey(anyString(), anyString())).andReturn(
                new xyz.greatapp.libs.service.ServiceResult(true, "", clientUser.toJSONObject().toString()));
        expect(clientUserRepository.updateApiKeyById(anyLong(), anyString())).andReturn(0);
        replay(clientUserRepository);
        return clientUserRepository;
    }

    private ClientUserRepository createClientUserRepositoryForEmail(ClientUser clientUser) throws Exception {
        final ClientUserRepository clientUserRepository = EasyMock.createMock(ClientUserRepository.class);
        expect(clientUserRepository.getByPhoneAndKey(anyString(), anyString())).andReturn(new xyz.greatapp.libs.service.ServiceResult(false, "", "{}"));
        expect(clientUserRepository.getByEmailAndPassword(anyString(), anyString())).andReturn(
                new xyz.greatapp.libs.service.ServiceResult(true, "", clientUser.toJSONObject().toString()));
        expect(clientUserRepository.updateApiKeyById(anyLong(), anyString())).andReturn(1);
        replay(clientUserRepository);
        return clientUserRepository;
    }

    private ClientUserRepository createClientUserRepositoryForEmailWhenNotUpdatingApiKey(ClientUser clientUser) throws Exception {
        final ClientUserRepository clientUserRepository = EasyMock.createMock(ClientUserRepository.class);
        expect(clientUserRepository.getByPhoneAndKey(anyString(), anyString())).andReturn(new xyz.greatapp.libs.service.ServiceResult(false, "", "{}"));
        expect(clientUserRepository.getByEmailAndPassword(anyString(), anyString())).andReturn(
                new xyz.greatapp.libs.service.ServiceResult(true, "", clientUser.toJSONObject().toString())
        );
        expect(clientUserRepository.updateApiKeyById(anyLong(), anyString())).andReturn(0);
        replay(clientUserRepository);
        return clientUserRepository;
    }

    private ClientRepository createClientRepository() throws Exception {
        final ClientRepository clientRepository = EasyMock.createMock(ClientRepository.class);
        replay(clientRepository);
        return clientRepository;
    }

    private ClientRepository createClientRepositoryForRegister() throws Exception {
        final ClientRepository clientRepository = EasyMock.createMock(ClientRepository.class);
        expect(clientRepository.insertIfDoesNotExist(anyString(), anyBoolean())).andReturn(new xyz.greatapp.libs.service.ServiceResult(true, "", new Client().toJSONObject().toString()));
        replay(clientRepository);
        return clientRepository;
    }

    private ClientUserRepository createClientUserRepository() throws Exception {
        ClientUserRepository clientUserRepository = createMock(ClientUserRepository.class);
        expect(clientUserRepository.insert((ClientUser) anyObject())).andReturn(1L);
        expect(clientUserRepository.getByClientId(anyLong())).andReturn(new xyz.greatapp.libs.service.ServiceResult(false, "", "{}"));
        replay(clientUserRepository);
        return clientUserRepository;
    }

    private ClientUserRepository createClientUserRepositoryWhenClientExists(ClientUser clientUser) throws Exception {
        ClientUserRepository clientUserRepository = createMock(ClientUserRepository.class);
        expect(clientUserRepository.getByClientId(anyLong())).andReturn(
                new xyz.greatapp.libs.service.ServiceResult(true, "", clientUser.toJSONObject().toString()));
        expect(clientUserRepository.updateSmsKey(anyString(), anyString())).andReturn(1);
        replay(clientUserRepository);
        return clientUserRepository;
    }

    private ClientUserRepository createClientUserRepositoryForUpdateSms() throws Exception {
        ClientUserRepository clientUserRepository = createMock(ClientUserRepository.class);
        expect(clientUserRepository.updateSmsKey(anyString(), anyString())).andReturn(1);
        replay(clientUserRepository);
        return clientUserRepository;
    }
}