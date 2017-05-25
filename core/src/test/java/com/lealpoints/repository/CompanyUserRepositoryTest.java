package com.lealpoints.repository;

import static com.lealpoints.repository.fixtures.CompanyUserRepositoryFixture.INSERT_COMPANY_AND_COMPANY_USER_WHERE_ACTIVE_IS_FALSE;
import static com.lealpoints.repository.fixtures.CompanyUserRepositoryFixture.INSERT_COMPANY_AND_COMPANY_USER_WHERE_ACTIVE_IS_TRUE;
import static com.lealpoints.repository.fixtures.CompanyUserRepositoryFixture.INSERT_COMPANY_AND_TWO_COMPANY_USERS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.lealpoints.db.queryagent.QueryAgent;
import com.lealpoints.model.CompanyUser;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class CompanyUserRepositoryTest extends BaseRepositoryTest
{

    private CompanyUserRepository _companyUserRepository;

    @Before
    public void setUp() throws Exception
    {
        try
        {
            _companyUserRepository = createCompanyUserRepository(getQueryAgent());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Test
    public void testClearActivationKey() throws Exception
    {
        executeFixture(INSERT_COMPANY_AND_COMPANY_USER_WHERE_ACTIVE_IS_FALSE);
        CompanyUser companyUser = getCompanyUserById(1);
        assertEquals("1234", companyUser.getActivationKey());
        _companyUserRepository.clearActivationKey("1234");
        companyUser = getCompanyUserById(1);
        assertNull(companyUser.getActivationKey());
    }

    @Test
    public void testSetTempPasswordByEmail() throws Exception
    {
        executeFixture(INSERT_COMPANY_AND_COMPANY_USER_WHERE_ACTIVE_IS_FALSE);
        CompanyUser beforeUpdateCompanyUser = getCompanyUserById(1);
        _companyUserRepository.updatePasswordByEmail("a@a.com", "newPassword", true);
        CompanyUser afterUpdateCompanyUser = getCompanyUserById(1);
        assertFalse(beforeUpdateCompanyUser.getMustChangePassword());
        assertNotEquals(beforeUpdateCompanyUser.getPassword(), afterUpdateCompanyUser.getPassword());
        assertTrue(afterUpdateCompanyUser.getMustChangePassword());
    }

    @Test
    public void testUpdatePasswordByEmail() throws Exception
    {
        executeFixture(INSERT_COMPANY_AND_COMPANY_USER_WHERE_ACTIVE_IS_FALSE);
        CompanyUser beforeUpdateCompanyUser = getCompanyUserById(1);
        _companyUserRepository.updatePasswordByEmail("a@a.com", "newPassword", false);
        CompanyUser afterUpdateCompanyUser = getCompanyUserById(1);
        assertNotEquals(beforeUpdateCompanyUser.getPassword(), afterUpdateCompanyUser.getPassword());
    }

    @Test
    public void testGetByCompanyUserIdApiKey() throws Exception
    {
        executeFixture(INSERT_COMPANY_AND_COMPANY_USER_WHERE_ACTIVE_IS_TRUE);
        CompanyUser companyUser = _companyUserRepository.getByCompanyUserIdApiKey(1, "ASDQWE");
        assertNotNull(companyUser);
    }

    @Test
    public void testGetByCompanyUserIdApiKeyWhenDoesNotExist() throws Exception
    {
        CompanyUser companyUser = _companyUserRepository.getByCompanyUserIdApiKey(1, "ASDQWE");
        assertNull(companyUser);
    }

    private CompanyUser getCompanyUserById(long companyUserId) throws Exception
    {
        CompanyUser companyUser = new CompanyUser();
        try (Statement st = getQueryAgent().getConnection().createStatement())
        {
            ResultSet resultSet = st.executeQuery("SELECT * FROM company_user WHERE company_user_id = " + companyUserId);
            if (resultSet.next())
            {
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

    private CompanyUserRepository createCompanyUserRepository(final QueryAgent queryAgent)
    {
        return new CompanyUserRepository()
        {
            @Override
            protected QueryAgent getQueryAgent() throws Exception
            {
                return queryAgent;
            }
        };
    }

}
