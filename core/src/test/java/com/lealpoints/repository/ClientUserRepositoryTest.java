package com.lealpoints.repository;

import com.lealpoints.db.queryagent.QueryAgent;
import com.lealpoints.model.ClientUser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.Statement;

import static com.lealpoints.repository.fixtures.ClientUserRepositoryFixture.INSERT_CLIENT;
import static com.lealpoints.repository.fixtures.ClientUserRepositoryFixture.INSERT_CLIENT_AND_CLIENT_USER;
import static com.lealpoints.repository.fixtures.ClientUserRepositoryFixture.INSERT_CLIENT_THAT_CAN_RECEIVE_SMS;
import static org.junit.Assert.*;

public class ClientUserRepositoryTest extends BaseRepositoryTest {

    private ClientUserRepository _clientUserRepository;

    @Before
    public void setUp() throws Exception {
        try {
            _clientUserRepository = createClientUserRepository(getQueryAgent());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testInsert() throws Exception {
        executeFixture(INSERT_CLIENT);
        ClientUser expectedClientUser = new ClientUser();
        expectedClientUser.setClientId(1);
        expectedClientUser.setName("pepe");
        expectedClientUser.setEmail("pepe@test.com");
        expectedClientUser.setPassword("password");
        expectedClientUser.setSmsKey("qwerty");
        final long clientUserId = _clientUserRepository.insert(expectedClientUser);
        ClientUser actualClientUser = getClientUserById(clientUserId);
        Assert.assertEquals(clientUserId, actualClientUser.getClientUserId());
        Assert.assertEquals(expectedClientUser.getClientId(), actualClientUser.getClientId());
        Assert.assertEquals(expectedClientUser.getName(), actualClientUser.getName());
        Assert.assertEquals(expectedClientUser.getEmail(), actualClientUser.getEmail());
        assertNotNull(actualClientUser.getPassword());
        assertNotNull(actualClientUser.getSmsKey());
    }

    @Test
    public void testUpdateSmsKey() throws Exception {
        executeFixture(INSERT_CLIENT_THAT_CAN_RECEIVE_SMS);
        int updatedRows = _clientUserRepository.updateSmsKey("1234", "6141112233");
        assertTrue(updatedRows == 1);
        ClientUser clientUser = getClientUserByPhoneAndSmsKey("6141112233", "1234");
        assertNotNull(clientUser);
        assertEquals("name", clientUser.getName());
        assertEquals("a@a.com", clientUser.getEmail());
    }

    @Test
    public void testGetByClientId() throws Exception {
        executeFixture(INSERT_CLIENT_AND_CLIENT_USER);
        ClientUser clientUser = _clientUserRepository.getByClientId(1);
        assertNotNull(clientUser);
        assertNotNull(clientUser.getSmsKey());
    }

    @Test
    public void testGetByPhoneAndKey() throws Exception {
        executeFixture(INSERT_CLIENT_AND_CLIENT_USER);
        ClientUser clientUser = _clientUserRepository.getByPhoneAndKey("6141112233", "qwerty");
        assertNotNull(clientUser);
        assertNotNull(clientUser.getSmsKey());
    }

    @Test
    public void testGetByEmailAndPassword() throws Exception {
        final String email = "a@a.com";
        final String password = "password";
        executeFixture(INSERT_CLIENT_AND_CLIENT_USER);
        ClientUser clientUser = _clientUserRepository.getByEmailAndPassword(email, password);
        assertNotNull(clientUser);
        Assert.assertEquals("a@a.com", clientUser.getEmail());
    }

    @Test
    public void testApiKeyByEmail() throws Exception {
        executeFixture(INSERT_CLIENT_AND_CLIENT_USER);
        ClientUser beforeUpdateClientUser = getClientUserById(1);
        _clientUserRepository.updateApiKeyById(1, "QWER");
        ClientUser afterUpdateClientUser = getClientUserById(1);
        assertNotEquals(beforeUpdateClientUser.getApiKey(), afterUpdateClientUser.getApiKey());
    }

    @Test
    public void testGetByCompanyUserIdApiKey() throws Exception {
        executeFixture(INSERT_CLIENT_AND_CLIENT_USER);
        ClientUser companyUser = _clientUserRepository.getByClientUserIdApiKey(1, "ASDQWE");
        assertNotNull(companyUser);
    }

    @Test
    public void testGetByCompanyUserIdApiKeyWhenDoesNotExist() throws Exception {
        ClientUser companyUser = _clientUserRepository.getByClientUserIdApiKey(1, "ASDQWE");
        assertNull(companyUser);
    }

    private ClientUser getClientUserById(long clientUserId) throws Exception {
        Statement st = null;
        ClientUser clientUser = null;
        try {
            st = getQueryAgent().getConnection().createStatement();
            ResultSet resultSet = st.executeQuery("SELECT * FROM client_user WHERE client_user_id = " + clientUserId);
            if(resultSet.next()) {
                clientUser = new ClientUser();
                clientUser.setClientUserId(resultSet.getLong("client_user_id"));
                clientUser.setClientId(resultSet.getLong("client_id"));
                clientUser.setName(resultSet.getString("name"));
                clientUser.setEmail(resultSet.getString("email"));
                clientUser.setPassword(resultSet.getString("password"));
                clientUser.setSmsKey(resultSet.getString("sms_key"));
                clientUser.setApiKey(resultSet.getString("api_key"));
            }
        }
        finally {
            if (st != null) {
                st.close();
            }
        }
        return clientUser;
    }

    private ClientUser getClientUserByPhoneAndSmsKey(String phone, String smsKey) throws Exception {
        Statement st = null;
        ClientUser clientUser = new ClientUser();
        try {
            st = getQueryAgent().getConnection().createStatement();
            ResultSet resultSet = st.executeQuery("SELECT * FROM client_user INNER JOIN client USING (client_id) " +
                "WHERE client.phone = '" + phone + "' AND client_user.sms_key = '" + smsKey + "';");
            if (resultSet.next()) {
                clientUser.setClientUserId(resultSet.getLong("client_user_id"));
                clientUser.setClientId(resultSet.getLong("client_id"));
                clientUser.setName(resultSet.getString("name"));
                clientUser.setEmail(resultSet.getString("email"));
                clientUser.setPassword(resultSet.getString("password"));
                clientUser.setSmsKey(resultSet.getString("sms_key"));
            }
        } finally {
            if (st != null) {
                st.close();
            }
        }
        return clientUser;
    }

    private ClientUserRepository createClientUserRepository(final QueryAgent queryAgent) {
        return new ClientUserRepository() {
            @Override
            protected QueryAgent getQueryAgent() throws Exception {
                return queryAgent;
            }
        };
    }
}
