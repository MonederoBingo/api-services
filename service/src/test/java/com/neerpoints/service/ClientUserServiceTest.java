package com.neerpoints.service;

import javax.mail.MessagingException;
import java.sql.SQLException;
import com.neerpoints.context.ThreadContext;
import com.neerpoints.context.ThreadContextService;
import com.neerpoints.db.QueryAgent;
import com.neerpoints.model.Client;
import com.neerpoints.model.ClientUser;
import com.neerpoints.repository.ClientRepository;
import com.neerpoints.repository.ClientUserRepository;
import com.neerpoints.service.model.ClientUserLogin;
import com.neerpoints.service.model.ClientUserRegistration;
import com.neerpoints.service.model.ServiceResult;
import com.neerpoints.util.Translations;
import org.easymock.EasyMock;
import org.junit.Test;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class ClientUserServiceTest {

    @Test
    public void testRegister() throws Exception {
        ClientUserRepository clientUserRepository = createClientUserRepository();
        ClientRepository clientRepository = createClientRepositoryForRegister();
        final QueryAgent queryAgent = createQueryAgent();

        ThreadContext threadContext = new ThreadContext();
        threadContext.setProdEnvironment(false);
        final ThreadContextService threadContextService = createThreadContextService(queryAgent, threadContext);
        ClientUserService clientUserService = new ClientUserService(clientUserRepository, clientRepository, threadContextService, null) {
            @Override
            String getTranslation(Translations.Message message) {
                return message.name();
            }

            @Override
            String generateAndSendSms(String phone) throws MessagingException {
                return "";
            }
        };
        ClientUserRegistration clientUserRegistration = new ClientUserRegistration();
        clientUserRegistration.setPhone("1234567890");
        ServiceResult<Long> serviceResult = clientUserService.register(clientUserRegistration);
        assertNotNull(serviceResult);
        assertTrue(serviceResult.getObject() > 0);
        verify(clientRepository, clientRepository);
    }

    @Test
    public void testRegisterWhenClientExists() throws Exception {
        final ClientUser clientUser = new ClientUser();
        clientUser.setClientUserId(2);
        ClientUserRepository clientUserRepository = createClientUserRepositoryWhenClientExists(clientUser);
        ClientRepository clientRepository = createClientRepositoryForRegister();
        final QueryAgent queryAgent = createQueryAgent();
        ThreadContext threadContext = new ThreadContext();
        threadContext.setProdEnvironment(false);
        final ThreadContextService threadContextService = createThreadContextService(queryAgent, threadContext);
        ClientUserService clientUserService = new ClientUserService(clientUserRepository, clientRepository, threadContextService, null){
            @Override
            String generateAndSendSms(String phone) throws MessagingException {
                return "";
            }
        };
        ClientUserRegistration clientUserRegistration = new ClientUserRegistration();
        clientUserRegistration.setPhone("1234567890");
        ServiceResult<Long> serviceResult = clientUserService.register(new ClientUserRegistration());
        assertNotNull(serviceResult);
        assertEquals(2, (long) serviceResult.getObject());
        verify(clientRepository, clientRepository);
    }

    @Test
    public void testRegisterWithInvalidPhone() {
        final ClientUserService clientUserService = new ClientUserService(null, null, null, null) {
            @Override
            String getTranslation(Translations.Message message) {
                return message.name();
            }

            @Override
            String generateAndSendSms(String phone) throws MessagingException {
                return "";
            }
        };
        ClientUserRegistration clientUserRegistration = new ClientUserRegistration();
        clientUserRegistration.setPhone("123");
        ServiceResult<Long> serviceResult = clientUserService.register(clientUserRegistration);
        assertNotNull(serviceResult);
        assertEquals(Translations.Message.PHONE_MUST_HAVE_10_DIGITS.name(), serviceResult.getMessage());
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
        final ClientUserService clientUserService =
            new ClientUserService(clientUserRepository, clientRepository, createMock(ThreadContextService.class), null){
                @Override
                String generateAndSendSms(String phone) throws MessagingException {
                    return "";
                }
            };

        ClientUserLogin clientUserLogin = new ClientUserLogin();
        clientUserLogin.setPhone("6141112233");
        clientUserLogin.setSmsKey("qwerty");
        clientUserLogin.setEmail("");
        clientUserLogin.setPassword("");
        ServiceResult serviceResult = clientUserService.login(clientUserLogin);
        assertNotNull(serviceResult);
        assertTrue(serviceResult.isSuccess());
        assertEquals(clientUser.getClientId(), serviceResult.getObject());
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
        final ClientUserService clientUserService =
            new ClientUserService(clientUserRepository, clientRepository, createMock(ThreadContextService.class), null){
                @Override
                String generateAndSendSms(String phone) throws MessagingException {
                    return "";
                }
            };

        ClientUserLogin clientUserLogin = new ClientUserLogin();
        clientUserLogin.setPhone("");
        clientUserLogin.setSmsKey("");
        clientUserLogin.setEmail("a@a.com");
        clientUserLogin.setPassword("password");
        ServiceResult serviceResult = clientUserService.login(clientUserLogin);
        assertNotNull(serviceResult);
        assertTrue(serviceResult.isSuccess());
        assertEquals(clientUser.getClientId(), serviceResult.getObject());
        verify(clientUserRepository, clientRepository);
    }

    @Test
    public void testResendKey() throws Exception {
        ThreadContext threadContext = new ThreadContext();
        threadContext.setProdEnvironment(false);
        ThreadContextService threadContextService = createThreadContextService(threadContext);
        ClientUserService clientUserService = new ClientUserService(createClientUserRepositoryForUpdateSms(), null, threadContextService, null) {
            @Override
            String generateAndSendSms(String phone) throws MessagingException {
                return "";
            }
        };
        ServiceResult<Boolean> serviceResult = clientUserService.resendKey("1234");
        assertNotNull(serviceResult);
        assertTrue(serviceResult.isSuccess());
        assertEquals("", serviceResult.getMessage());
        assertTrue(serviceResult.getObject());
    }

    private ThreadContextService createThreadContextService(QueryAgent queryAgent, ThreadContext threadContext) throws SQLException {
        ThreadContextService threadContextService = createMock(ThreadContextService.class);
        expect(threadContextService.getQueryAgent()).andReturn(queryAgent).times(2);
        expect(threadContextService.getThreadContext()).andReturn(threadContext).times(1);
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

    private ClientUserRepository createClientUserRepositoryForPhone(ClientUser clientUser) throws Exception {
        final ClientUserRepository clientUserRepository = EasyMock.createMock(ClientUserRepository.class);
        expect(clientUserRepository.getByPhoneAndKey(anyString(), anyString())).andReturn(clientUser);
        replay(clientUserRepository);
        return clientUserRepository;
    }

    private ClientUserRepository createClientUserRepositoryForEmail(ClientUser clientUser) throws Exception {
        final ClientUserRepository clientUserRepository = EasyMock.createMock(ClientUserRepository.class);
        expect(clientUserRepository.getByPhoneAndKey(anyString(), anyString())).andReturn(null);
        expect(clientUserRepository.getByEmailAndPassword(anyString(), anyString())).andReturn(clientUser);
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
        expect(clientRepository.insertIfDoesNotExist(anyString())).andReturn(new Client());
        replay(clientRepository);
        return clientRepository;
    }

    private ClientUserRepository createClientUserRepository() throws Exception {
        ClientUserRepository clientUserRepository = createMock(ClientUserRepository.class);
        expect(clientUserRepository.insert((ClientUser) anyObject())).andReturn(1l);
        expect(clientUserRepository.getByClientId(anyLong())).andReturn(null);
        replay(clientUserRepository);
        return clientUserRepository;
    }

    private ClientUserRepository createClientUserRepositoryWhenClientExists(ClientUser clientUser) throws Exception {
        ClientUserRepository clientUserRepository = createMock(ClientUserRepository.class);
        expect(clientUserRepository.getByClientId(anyLong())).andReturn(clientUser);
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