package com.lealpoints.repository;

import com.lealpoints.db.queryagent.QueryAgent;
import com.lealpoints.model.CompanyUser;
import org.junit.Before;
import org.junit.Test;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import static com.lealpoints.repository.fixtures.CompanyUserRepositoryFixture.INSERT_COMPANY_AND_TWO_COMPANY_USERS;
import static com.lealpoints.repository.fixtures.CompanyUserRepositoryFixture.INSERT_COMPANY;
import static com.lealpoints.repository.fixtures.CompanyUserRepositoryFixture.INSERT_COMPANY_AND_COMPANY_USER_WHERE_ACTIVE_IS_FALSE;
import static com.lealpoints.repository.fixtures.CompanyUserRepositoryFixture.INSERT_COMPANY_AND_COMPANY_USER_WHERE_ACTIVE_IS_TRUE;
import static org.junit.Assert.*;

public class CompanyUserRepositoryTest extends BaseRepositoryTest {

    private CompanyUserRepository _companyUserRepository;

    @Before
    public void setUp() throws Exception {
        try {
            _companyUserRepository = createCompanyUserRepository(getQueryAgent());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testInsert() throws Exception {
        final int companyIdFromFixture = 1;
        executeFixture(INSERT_COMPANY);
        CompanyUser expectedCompanyUser = new CompanyUser();
        expectedCompanyUser.setCompanyId(companyIdFromFixture);
        expectedCompanyUser.setName("pepe");
        expectedCompanyUser.setPassword("password");
        expectedCompanyUser.setEmail("pepe@test.com");
        expectedCompanyUser.setActive(false);
        expectedCompanyUser.setActivationKey("1234");
        expectedCompanyUser.setLanguage("es");
        expectedCompanyUser.setMustChangePassword(false);
        final long companyUserId = _companyUserRepository.insert(expectedCompanyUser);
        CompanyUser actualCompanyUser = getCompanyUserById(companyUserId);
        assertEquals(companyUserId, actualCompanyUser.getCompanyUserId());
        assertEquals(expectedCompanyUser.getCompanyId(), actualCompanyUser.getCompanyId());
        assertEquals(expectedCompanyUser.getName(), actualCompanyUser.getName());
        assertEquals(expectedCompanyUser.getEmail(), actualCompanyUser.getEmail());
        assertNotNull(actualCompanyUser.getPassword());
        assertEquals(expectedCompanyUser.isActive(), actualCompanyUser.isActive());
        assertEquals(expectedCompanyUser.getActivationKey(), actualCompanyUser.getActivationKey());
        assertEquals(expectedCompanyUser.getMustChangePassword(), actualCompanyUser.getMustChangePassword());
    }

    @Test
    public void testGetByEmailAndPassword() throws Exception {
        final String email = "a@a.com";
        final String password = "password";
        executeFixture(INSERT_COMPANY_AND_COMPANY_USER_WHERE_ACTIVE_IS_TRUE);
        CompanyUser companyUser = _companyUserRepository.getByEmailAndPassword(email, password);
        assertNotNull(companyUser);
        assertEquals("a@a.com", companyUser.getEmail());
        assertEquals("es", companyUser.getLanguage());
        assertFalse(companyUser.getMustChangePassword());
    }

    @Test
    public void testGetByEmail() throws Exception {
        executeFixture(INSERT_COMPANY_AND_COMPANY_USER_WHERE_ACTIVE_IS_TRUE);
        CompanyUser companyUser = _companyUserRepository.getByEmail("a@a.com");
        assertNotNull(companyUser);
        assertEquals("a@a.com", companyUser.getEmail());
        assertTrue(companyUser.isActive());
        assertEquals("1234", companyUser.getActivationKey());
        assertEquals("es", companyUser.getLanguage());
    }

    @Test
    public void testGetByEmailWhenDoesNotExist() throws Exception {
        CompanyUser companyUser = _companyUserRepository.getByEmail("a@a.com");
        assertNull(companyUser);
    }

    @Test
    public void testUpdateActivateByActivationKey() throws Exception {
        executeFixture(INSERT_COMPANY_AND_COMPANY_USER_WHERE_ACTIVE_IS_FALSE);
        CompanyUser companyUser = getCompanyUserById(1);
        assertFalse(companyUser.isActive());
        _companyUserRepository.updateActivateByActivationKey("1234");
        companyUser = getCompanyUserById(1);
        assertTrue(companyUser.isActive());
    }

    @Test
    public void testClearActivationKey() throws Exception {
        executeFixture(INSERT_COMPANY_AND_COMPANY_USER_WHERE_ACTIVE_IS_FALSE);
        CompanyUser companyUser = getCompanyUserById(1);
        assertEquals("1234", companyUser.getActivationKey());
        _companyUserRepository.clearActivationKey("1234");
        companyUser = getCompanyUserById(1);
        assertNull(companyUser.getActivationKey());
    }

    @Test
    public void testSetTempPasswordByEmail() throws Exception {
        executeFixture(INSERT_COMPANY_AND_COMPANY_USER_WHERE_ACTIVE_IS_FALSE);
        CompanyUser beforeUpdateCompanyUser = getCompanyUserById(1);
        _companyUserRepository.updatePasswordByEmail("a@a.com", "newPassword", true);
        CompanyUser afterUpdateCompanyUser = getCompanyUserById(1);
        assertFalse(beforeUpdateCompanyUser.getMustChangePassword());
        assertNotEquals(beforeUpdateCompanyUser.getPassword(), afterUpdateCompanyUser.getPassword());
        assertTrue(afterUpdateCompanyUser.getMustChangePassword());
    }

    @Test
    public void testUpdatePasswordByEmail() throws Exception {
        executeFixture(INSERT_COMPANY_AND_COMPANY_USER_WHERE_ACTIVE_IS_FALSE);
        CompanyUser beforeUpdateCompanyUser = getCompanyUserById(1);
        _companyUserRepository.updatePasswordByEmail("a@a.com", "newPassword", false);
        CompanyUser afterUpdateCompanyUser = getCompanyUserById(1);
        assertNotEquals(beforeUpdateCompanyUser.getPassword(), afterUpdateCompanyUser.getPassword());
    }

    @Test
    public void testApiKeyByEmail() throws Exception {
        executeFixture(INSERT_COMPANY_AND_COMPANY_USER_WHERE_ACTIVE_IS_FALSE);
        CompanyUser beforeUpdateCompanyUser = getCompanyUserById(1);
        _companyUserRepository.updateApiKeyByEmail("a@a.com", "QWER");
        CompanyUser afterUpdateCompanyUser = getCompanyUserById(1);
        assertNotEquals(beforeUpdateCompanyUser.getApiKey(), afterUpdateCompanyUser.getApiKey());
    }

    @Test
    public void testGetByCompanyUserIdApiKey() throws Exception {
        executeFixture(INSERT_COMPANY_AND_COMPANY_USER_WHERE_ACTIVE_IS_TRUE);
        CompanyUser companyUser = _companyUserRepository.getByCompanyUserIdApiKey(1, "ASDQWE");
        assertNotNull(companyUser);
    }

    @Test
    public void testGetByCompanyUserIdApiKeyWhenDoesNotExist() throws Exception {
        CompanyUser companyUser = _companyUserRepository.getByCompanyUserIdApiKey(1, "ASDQWE");
        assertNull(companyUser);
    }

    @Test
    public void testGetByCompanyId() throws Exception {
        executeFixture(INSERT_COMPANY_AND_TWO_COMPANY_USERS);
        List<CompanyUser> companyUserList = _companyUserRepository.getByCompanyId(1);
        assertNotNull(companyUserList);
        assertEquals(2, companyUserList.size());
        assertEquals("name", companyUserList.get(0).getName());
        assertEquals("a@a.com", companyUserList.get(0).getEmail());
        assertEquals("second name", companyUserList.get(1).getName());
        assertEquals("f@a.com", companyUserList.get(1).getEmail());
    }

    private CompanyUser getCompanyUserById(long companyUserId) throws Exception {
        CompanyUser companyUser = new CompanyUser();
        try (Statement st = getQueryAgent().getConnection().createStatement()) {
            ResultSet resultSet = st.executeQuery("SELECT * FROM company_user WHERE company_user_id = " + companyUserId);
            if(resultSet.next()) {
                companyUser.setCompanyUserId(resultSet.getLong("company_user_id"));
                companyUser.setCompanyId(resultSet.getLong("company_id"));
                companyUser.setName(resultSet.getString("name"));
                companyUser.setEmail(resultSet.getString("email"));
                companyUser.setPassword(resultSet.getString("password"));
                companyUser.setActive(resultSet.getBoolean("active"));
                companyUser.setActivationKey(resultSet.getString("activation_key"));
                companyUser.setLanguage(resultSet.getString("language"));
                companyUser.setMustChangePassword(resultSet.getBoolean("must_change_password"));
                companyUser.setApiKey(resultSet.getString("api_key"));
            }
        }
        return companyUser;
    }

    private CompanyUserRepository createCompanyUserRepository(final QueryAgent queryAgent) {
        return new CompanyUserRepository() {
            @Override
            protected QueryAgent getQueryAgent() throws Exception {
                return queryAgent;
            }
        };
    }

}