package com.lealpoints.repository;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import com.lealpoints.db.QueryAgent;
import com.lealpoints.model.Client;
import com.lealpoints.model.CompanyClientMapping;
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
        List<CompanyClientMapping> clientPointsList = _clientRepository.getByCompanyId(1);
        assertNotNull(clientPointsList);
        assertEquals(2, clientPointsList.size());
        assertEquals(100, clientPointsList.get(0).getPoints(), 0.00);
        assertEquals(200, clientPointsList.get(1).getPoints(), 0.00);
    }

    @Test
    public void testGetByCompanyIdPhone() throws Exception {
        insertFixture("client_repository_get_by_company_id_phone.sql");
        CompanyClientMapping clientPoints = _clientRepository.getByCompanyIdPhone(1, "1234567890");
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
        Client client = _clientRepository.insertIfDoesNotExist("1234", true);
        assertNotNull(client);
    }

    @Test
    public void testInsertIfDoesNotExistWhenDoes() throws Exception {
        insertFixture("client_repository_insert_when_exists.sql");
        Client client = _clientRepository.insertIfDoesNotExist("1234", true);
        assertNotNull(client);
        Assert.assertEquals(1, client.getClientId());
        Assert.assertEquals("1234", client.getPhone());
    }

    @Test
    public void testUpdateCanReceivePromoSms() throws Exception {
        insertFixture("client_repository_test_fixture_for_update.sql");
        Client clientBeforeUpdate = getClientById(1);
        assertTrue(clientBeforeUpdate.canReceivePromotionSms());
        int updatedRows = _clientRepository.updateCanReceivePromoSms(1, false);
        assertTrue(updatedRows == 1);
        Client clientAfterUpdate = getClientById(1);
        assertNotNull(clientAfterUpdate);
        assertFalse(clientAfterUpdate.canReceivePromotionSms());
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
                client.setCanReceivePromotionSms(resultSet.getBoolean("can_receive_promo_sms"));
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