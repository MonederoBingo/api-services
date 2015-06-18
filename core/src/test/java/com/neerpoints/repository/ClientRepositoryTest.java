package com.neerpoints.repository;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import com.neerpoints.db.QueryAgent;
import com.neerpoints.model.Client;
import com.neerpoints.model.ClientPoints;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ClientRepositoryTest extends BaseRepositoryTest {
    private ClientRepository _clientRepository;

    @Before
    public void setUp() throws Exception {
        _clientRepository = createClientRepository(getQueryAgent());
    }

    @Test
    public void testInsert() throws Exception {
        Client expectedClient = new Client();
        expectedClient.setPhone("6141112233");
        final long clientId = _clientRepository.insert(expectedClient);
        Client actualClient = getClientById(clientId);
        Assert.assertEquals(clientId, actualClient.getClientId());
        Assert.assertEquals(expectedClient.getPhone(), actualClient.getPhone());
    }

    @Test
    public void testGetByCompanyId() throws Exception {
        insertFixture("client_repository_get_by_company_id.sql");
        List<ClientPoints> clientPointsList = _clientRepository.getByCompanyId(1);
        assertNotNull(clientPointsList);
        assertEquals(2, clientPointsList.size());
    }

    @Test
    public void testGetByCompanyIdPhone() throws Exception {
        insertFixture("client_repository_get_by_company_id_phone.sql");
        ClientPoints clientPoints = _clientRepository.getByCompanyIdPhone(1, "1234567890");
        assertNotNull(clientPoints);
        assertEquals(1200, clientPoints.getPoints(), 0.00);
    }

    @Test
    public void testGetByPhone() throws Exception {
        insertFixture("client_repository_get_by_phone.sql");
        Client client = _clientRepository.getByPhone("1234");
        assertNotNull(client);
        assertTrue(client.getClientId() > 0);
        Assert.assertEquals("1234", client.getPhone());
    }

    @Test
    public void testInsertIfDoesNotExistWhenDoNot() throws Exception {
        Client client = _clientRepository.insertIfDoesNotExist("1234");
        assertNotNull(client);
    }

    @Test
    public void testInsertIfDoesNotExistWhenDoes() throws Exception {
        insertFixture("client_repository_insert_when_exists.sql");
        Client client = _clientRepository.insertIfDoesNotExist("1234");
        assertNotNull(client);
        Assert.assertEquals(1, client.getClientId());
        Assert.assertEquals("1234", client.getPhone());
    }


    private Client getClientById(long clientId) throws Exception {
        Statement st = null;
        Client client = new Client();
        try {
            st = getQueryAgent().getConnection().createStatement();
            ResultSet resultSet = st.executeQuery("SELECT * FROM client WHERE client_id = " + clientId);
            if (resultSet.next()) {
                client.setClientId(resultSet.getLong("client_id"));
                client.setPhone(resultSet.getString("phone"));
            }
        } finally {
            if (st != null) {
                st.close();
            }
        }
        return client;
    }

    private ClientRepository createClientRepository(final QueryAgent queryAgent) {
        return new ClientRepository() {
            @Override
            protected QueryAgent getQueryAgent() throws Exception {
                return queryAgent;
            }
        };
    }
}