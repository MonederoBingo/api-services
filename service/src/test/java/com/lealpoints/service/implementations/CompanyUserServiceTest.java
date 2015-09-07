package com.lealpoints.service.implementations;

import javax.mail.MessagingException;
import java.sql.SQLException;
import com.lealpoints.context.ThreadContextService;
import com.lealpoints.db.QueryAgent;
import com.lealpoints.model.Company;
import com.lealpoints.model.CompanyUser;
import com.lealpoints.repository.CompanyRepository;
import com.lealpoints.repository.CompanyUserRepository;
import com.lealpoints.service.model.CompanyLoginResult;
import com.lealpoints.service.model.CompanyUserLogin;
import com.lealpoints.service.model.CompanyUserPasswordChanging;
import com.lealpoints.service.model.ServiceResult;
import com.lealpoints.util.Translations;
import org.easymock.EasyMock;
import org.junit.Test;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class CompanyUserServiceTest {

    @Test
    public void testLoginUser() throws Exception {
        CompanyUser companyUser = createCompanyUser(1, 1, "name", "a@a.com", "password", true, "es", true);
        final CompanyUserRepository companyUserRepository = createCompanyUserRepository(companyUser);
        CompanyRepository companyRepository = createCompanyRepository();
        final CompanyUserServiceImpl companyUserService = new CompanyUserServiceImpl(companyUserRepository, null, null, companyRepository, null);
        replay(companyUserRepository);

        CompanyUserLogin companyUserLogin = new CompanyUserLogin();
        companyUserLogin.setEmail("a@a.com");
        companyUserLogin.setPassword("password");
        ServiceResult<CompanyLoginResult> serviceResult = companyUserService.loginUser(companyUserLogin);
        assertNotNull(serviceResult);
        assertTrue(serviceResult.isSuccess());
        assertEquals("", serviceResult.getMessage());
        assertNotNull(serviceResult.getObject());
        CompanyLoginResult loginResult = serviceResult.getObject();
        assertTrue(loginResult.isActive());
        assertEquals(1, loginResult.getCompanyId());
        assertEquals("es", loginResult.getLanguage());
        assertTrue(loginResult.isMustChangePassword());
        assertFalse(loginResult.getApiKey().isEmpty());
        verify(companyUserRepository, companyRepository);
    }

    @Test
    public void testUserLoginWithoutEmail() throws Exception {
        final CompanyUserServiceImpl companyUserService = new CompanyUserServiceImpl(null, null, null, null, null) {
            @Override
            public String getTranslation(Translations.Message message) {
                return message.name();
            }
        };
        CompanyUserLogin companyUserLogin = new CompanyUserLogin();
        companyUserLogin.setEmail("");
        companyUserLogin.setPassword("password");
        ServiceResult<CompanyLoginResult> serviceResult = companyUserService.loginUser(companyUserLogin);
        assertNotNull(serviceResult);
        assertFalse(serviceResult.isSuccess());
        assertEquals(Translations.Message.EMAIL_IS_EMPTY.name(), serviceResult.getMessage());
        assertNull(serviceResult.getObject());
    }

    @Test
    public void testUserLoginWithoutPassword() throws Exception {
        final CompanyUserServiceImpl companyUserService = new CompanyUserServiceImpl(null, null, null, null, null) {
            @Override
            public String getTranslation(Translations.Message message) {
                return message.name();
            }
        };
        CompanyUserLogin companyUserLogin = new CompanyUserLogin();
        companyUserLogin.setEmail("a@a.com");
        companyUserLogin.setPassword("");
        ServiceResult<CompanyLoginResult> serviceResult = companyUserService.loginUser(companyUserLogin);
        assertNotNull(serviceResult);
        assertFalse(serviceResult.isSuccess());
        assertEquals(Translations.Message.PASSWORD_IS_EMPTY.name(), serviceResult.getMessage());
        assertNull(serviceResult.getObject());
    }

    @Test
    public void testUserLoginWhenIsNotActive() throws Exception {
        CompanyUser companyUser = createCompanyUser(1, 1, "name", "a@a.com", "password", false, "es", true);
        final CompanyUserRepository companyUserRepository = createCompanyUserRepositoryIsNotActive(companyUser);
        final CompanyUserServiceImpl companyUserService = new CompanyUserServiceImpl(companyUserRepository, null, null, null, null) {
            @Override
            public String getTranslation(Translations.Message message) {
                return message.name();
            }
        };
        replay(companyUserRepository);

        CompanyUserLogin companyUserLogin = new CompanyUserLogin();
        companyUserLogin.setEmail("a@a.com");
        companyUserLogin.setPassword("password");
        ServiceResult<CompanyLoginResult> serviceResult = companyUserService.loginUser(companyUserLogin);
        assertNotNull(serviceResult);
        assertFalse(serviceResult.isSuccess());
        assertEquals(Translations.Message.YOUR_USER_IS_NOT_ACTIVE.name(), serviceResult.getMessage());
        assertNotNull(serviceResult.getObject());
        CompanyLoginResult loginResult = serviceResult.getObject();
        assertFalse(loginResult.isActive());
        verify(companyUserRepository);
    }

    @Test
    public void testUserLoginWhenNotUpdatingApiKey() throws Exception {
        CompanyUser companyUser = createCompanyUser(1, 1, "name", "a@a.com", "password", true, "es", true);
        final CompanyUserRepository companyUserRepository = createCompanyUserRepositoryWhenNotUpdatingApiKey(companyUser);
        final CompanyUserServiceImpl companyUserService = new CompanyUserServiceImpl(companyUserRepository, null, null, null, null) {
            @Override
            public String getTranslation(Translations.Message message) {
                return message.name();
            }
        };
        replay(companyUserRepository);

        CompanyUserLogin companyUserLogin = new CompanyUserLogin();
        companyUserLogin.setEmail("a@a.com");
        companyUserLogin.setPassword("password");
        ServiceResult<CompanyLoginResult> serviceResult = companyUserService.loginUser(companyUserLogin);
        assertNotNull(serviceResult);
        assertFalse(serviceResult.isSuccess());
        assertEquals(Translations.Message.COMMON_USER_ERROR.name(), serviceResult.getMessage());
        verify(companyUserRepository);
    }

    @Test
    public void activateUser() throws Exception {
        CompanyUserRepository companyUserRepository = createCompanyUserRepositoryForActivate();
        final QueryAgent queryAgent = createQueryAgent();
        final ThreadContextService threadContextService = createThreadContextService(queryAgent);
        CompanyUserServiceImpl companyUserService = new CompanyUserServiceImpl(companyUserRepository, threadContextService, null, null, null) {
            @Override
            public String getTranslation(Translations.Message message) {
                return message.name();
            }
        };
        ServiceResult serviceResult = companyUserService.activateUser("1234");
        assertNotNull(serviceResult);
        assertTrue(serviceResult.isSuccess());
    }

    @Test
    public void sendActivationEmail() throws Exception {
        CompanyUserRepository companyUserRepository = createCompanyUserRepositoryForSendActivation();
        CompanyUserServiceImpl companyUserService = new CompanyUserServiceImpl(companyUserRepository, null, null, null, createCompanyService()) {
            @Override
            public String getTranslation(Translations.Message message) {
                return message.name();
            }
        };
        ServiceResult serviceResult = companyUserService.sendActivationEmail("a@a.com");
        assertNotNull(serviceResult);
        assertTrue(serviceResult.isSuccess());
    }

    private CompanyServiceImpl createCompanyService() throws MessagingException {
        CompanyServiceImpl companyService = createNiceMock(CompanyServiceImpl.class);
        companyService.sendActivationEmail(anyString(), anyString());
        expectLastCall();
        replay(companyService);
        return companyService;
    }

    @Test
    public void testSendActivationEmailWhenEmailDoesNotExist() throws Exception {
        CompanyUserRepository companyUserRepository = createCompanyUserRepositoryForSendingActivationWhenEmailDoesNotExist();
        CompanyUserServiceImpl companyUserService = new CompanyUserServiceImpl(companyUserRepository, null, null, null, createCompanyService()) {
            @Override
            public String getTranslation(Translations.Message message) {
                return message.name();
            }
        };
        ServiceResult serviceResult = companyUserService.sendActivationEmail("a@a.com");
        assertNotNull(serviceResult);
        assertFalse(serviceResult.isSuccess());
        assertEquals(Translations.Message.THIS_EMAIL_DOES_NOT_EXIST.name(), serviceResult.getMessage());
    }

    @Test
    public void testSendTestPasswordEmail() throws Exception {
        CompanyUserRepository companyUserRepository = createCompanyUserRepositoryForSendingTempPasswordEmail();
        CompanyUserServiceImpl companyUserService = new CompanyUserServiceImpl(companyUserRepository, null, null, null, null) {
            @Override
            void sendTempPasswordEmail(String email, String tempPassword) throws MessagingException {

            }

            @Override
            public String getTranslation(Translations.Message message) {
                return message.name();
            }
        };

        ServiceResult serviceResult = companyUserService.sendTempPasswordEmail("a@a.com");
        assertNotNull(serviceResult);
        assertTrue(serviceResult.isSuccess());
        assertEquals(Translations.Message.WE_HAVE_SENT_YOU_A_NEW_PASSWORD_TO_YOUR_EMAIL.name(), serviceResult.getMessage());
    }

    @Test
    public void testChangePassword() throws Exception {
        CompanyUserRepository companyUserRepository = createCompanyUserRepositoryForSendingTempPasswordEmail();
        CompanyUserServiceImpl companyUserService = new CompanyUserServiceImpl(companyUserRepository, null, null, null, null) {
            @Override
            void sendTempPasswordEmail(String email, String tempPassword) throws MessagingException {

            }

            @Override
            public String getTranslation(Translations.Message message) {
                return message.name();
            }
        };

        final CompanyUserPasswordChanging passwordChanging = new CompanyUserPasswordChanging();
        passwordChanging.setNewPassword("123456");
        passwordChanging.setPasswordConfirmation("123456");
        ServiceResult serviceResult = companyUserService.changePassword(passwordChanging);
        assertNotNull(serviceResult);
        assertTrue(serviceResult.isSuccess());
        assertEquals(Translations.Message.YOUR_PASSWORD_HAS_BEEN_CHANGED.name(), serviceResult.getMessage());
    }

    @Test
    public void testChangePasswordWithInvalidPasswords() throws Exception {
        CompanyUserRepository companyUserRepository = createCompanyUserRepositoryForSendingTempPasswordEmail();
        CompanyUserServiceImpl companyUserService = new CompanyUserServiceImpl(companyUserRepository, null, null, null, null) {
            @Override
            void sendTempPasswordEmail(String email, String tempPassword) throws MessagingException {

            }

            @Override
            public String getTranslation(Translations.Message message) {
                return message.name();
            }
        };

        final CompanyUserPasswordChanging passwordChanging = new CompanyUserPasswordChanging();
        passwordChanging.setNewPassword("12345");
        passwordChanging.setPasswordConfirmation("12345");
        ServiceResult serviceResult = companyUserService.changePassword(passwordChanging);
        assertNotNull(serviceResult);
        assertFalse(serviceResult.isSuccess());
        assertEquals(Translations.Message.PASSWORD_MUST_HAVE_AT_LEAST_6_CHARACTERS.name(), serviceResult.getMessage());
    }

    @Test
    public void testChangePasswordWithNotSamePasswords() throws Exception {
        CompanyUserRepository companyUserRepository = createCompanyUserRepositoryForSendingTempPasswordEmail();
        CompanyUserServiceImpl companyUserService = new CompanyUserServiceImpl(companyUserRepository, null, null, null, null) {
            @Override
            void sendTempPasswordEmail(String email, String tempPassword) throws MessagingException {
            }

            @Override
            public String getTranslation(Translations.Message message) {
                return message.name();
            }
        };

        final CompanyUserPasswordChanging passwordChanging = new CompanyUserPasswordChanging();
        passwordChanging.setNewPassword("123457");
        passwordChanging.setPasswordConfirmation("123456");
        ServiceResult serviceResult = companyUserService.changePassword(passwordChanging);
        assertNotNull(serviceResult);
        assertFalse(serviceResult.isSuccess());
        assertEquals(Translations.Message.PASSWORD_AND_CONFIRMATION_ARE_DIFFERENT.name(), serviceResult.getMessage());
    }

    private CompanyRepository createCompanyRepository() throws Exception {
        CompanyRepository companyRepository = createMock(CompanyRepository.class);
        expect(companyRepository.getByCompanyId(anyLong())).andReturn(new Company());
        replay(companyRepository);
        return companyRepository;
    }

    private CompanyUserRepository createCompanyUserRepositoryForActivate() throws Exception {
        CompanyUserRepository companyUserRepository = createMock(CompanyUserRepository.class);
        expect(companyUserRepository.updateActivateByActivationKey(anyString())).andReturn(1);
        expect(companyUserRepository.clearActivationKey(anyString())).andReturn(1);
        replay(companyUserRepository);
        return companyUserRepository;
    }

    private CompanyUserRepository createCompanyUserRepositoryForSendActivation() throws Exception {
        CompanyUserRepository companyUserRepository = createMock(CompanyUserRepository.class);
        expect(companyUserRepository.getByEmail(anyString())).andReturn(new CompanyUser());
        replay(companyUserRepository);
        return companyUserRepository;
    }

    private CompanyUserRepository createCompanyUserRepositoryForSendingActivationWhenEmailDoesNotExist() throws Exception {
        CompanyUserRepository companyUserRepository = createMock(CompanyUserRepository.class);
        expect(companyUserRepository.getByEmail(anyString())).andReturn(null);
        replay(companyUserRepository);
        return companyUserRepository;
    }

    private CompanyUserRepository createCompanyUserRepositoryForSendingTempPasswordEmail() throws Exception {
        CompanyUserRepository companyUserRepository = createMock(CompanyUserRepository.class);
        expect(companyUserRepository.getByEmail(anyString())).andReturn(new CompanyUser());
        expect(companyUserRepository.updatePasswordByEmail(anyString(), anyString(), anyBoolean())).andReturn(1);
        replay(companyUserRepository);
        return companyUserRepository;
    }

    private CompanyUserRepository createCompanyUserRepository(CompanyUser companyUser) throws Exception {
        final CompanyUserRepository companyUserRepository = EasyMock.createMock(CompanyUserRepository.class);
        expect(companyUserRepository.getByEmailAndPassword(anyString(), anyString())).andReturn(companyUser);
        expect(companyUserRepository.updateApiKeyByEmail(anyString(), anyString())).andReturn(1);
        return companyUserRepository;
    }

    private CompanyUserRepository createCompanyUserRepositoryIsNotActive(CompanyUser companyUser) throws Exception {
        final CompanyUserRepository companyUserRepository = EasyMock.createMock(CompanyUserRepository.class);
        expect(companyUserRepository.getByEmailAndPassword(anyString(), anyString())).andReturn(companyUser);
        return companyUserRepository;
    }

    private CompanyUserRepository createCompanyUserRepositoryWhenNotUpdatingApiKey(CompanyUser companyUser) throws Exception {
        final CompanyUserRepository companyUserRepository = EasyMock.createMock(CompanyUserRepository.class);
        expect(companyUserRepository.getByEmailAndPassword(anyString(), anyString())).andReturn(companyUser);
        expect(companyUserRepository.updateApiKeyByEmail(anyString(), anyString())).andReturn(0);
        return companyUserRepository;
    }

    private ThreadContextService createThreadContextService(QueryAgent queryAgent) throws SQLException {
        ThreadContextService threadContextService = createMock(ThreadContextService.class);

        expect(threadContextService.getQueryAgent()).andReturn(queryAgent).times(2);
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

    private CompanyUser createCompanyUser(int companyUserId, int companyId, String name, String email, String password, boolean active,
        String language, boolean mustChangePassword) {
        CompanyUser companyUser = new CompanyUser();
        companyUser.setCompanyUserId(companyUserId);
        companyUser.setCompanyId(companyId);
        companyUser.setName(name);
        companyUser.setEmail(email);
        companyUser.setPassword(password);
        companyUser.setActive(active);
        companyUser.setLanguage(language);
        companyUser.setMustChangePassword(mustChangePassword);
        return companyUser;
    }
}