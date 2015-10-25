package com.lealpoints.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import com.lealpoints.db.queryagent.QueryAgent;
import com.lealpoints.model.Client;
import com.lealpoints.model.CompanyClientMapping;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.postgresql.util.PSQLException;

import static org.junit.Assert.*;

public class CompanyClientMappingRepositoryTest extends BaseRepositoryTest {
    private CompanyClientMappingRepository _companyClientMappingRepository;

    @Before
    public void setUp() throws Exception {
        _companyClientMappingRepository = createCompanyClientMappingRepository(getQueryAgent());
    }

    @Test
    public void testGetByCompanyIdClientId() throws Exception {
        final int companyClientMappingIdFromFixture = 1;
        final int companyIdFromFixture = 1;
        final int clientIdFromFixture = 1;
        insertFixture("company_client_mapping_repository_get_by_company_id_client_id.sql");

        CompanyClientMapping companyClientMapping = _companyClientMappingRepository.getByCompanyIdClientId(companyIdFromFixture, clientIdFromFixture);
        assertNotNull(companyClientMapping);
        assertEquals(companyClientMappingIdFromFixture, companyClientMapping.getCompanyClientMappingId());
    }

    @Test
    public void testGetByCompanyIdClientIdWhenDoesNotExist() throws Exception {
        assertNull(_companyClientMappingRepository.getByCompanyIdClientId(1, 1));
    }

    @Test
    public void testInsert() throws Exception {
        insertFixture("company_client_mapping_repository_insert.sql");
        CompanyClientMapping expectedCompanyClientMapping = new CompanyClientMapping();
        expectedCompanyClientMapping.setCompanyId(1);
        Client client = new Client();
        client.setClientId(1);
        expectedCompanyClientMapping.setClient(client);
        final long companyClientMappingId = _companyClientMappingRepository.insert(expectedCompanyClientMapping);
        CompanyClientMapping actualCompanyClientMapping = getCompanyClientMappingById(companyClientMappingId);
        Assert.assertEquals(companyClientMappingId, actualCompanyClientMapping.getCompanyClientMappingId());
        Assert.assertEquals(expectedCompanyClientMapping.getCompanyId(), actualCompanyClientMapping.getCompanyId());
        Assert.assertEquals(expectedCompanyClientMapping.getClient().getClientId(), actualCompanyClientMapping.getClient().getClientId());
    }

    @Test(expected = PSQLException.class)
    public void testInsertViolatingUnique() throws Exception {
        insertFixture("company_client_mapping_repository_insert.sql");
        CompanyClientMapping expectedCompanyClientMapping = new CompanyClientMapping();
        expectedCompanyClientMapping.setCompanyId(1);
        Client client = new Client();
        client.setClientId(1);
        expectedCompanyClientMapping.setClient(client);
        _companyClientMappingRepository.insert(expectedCompanyClientMapping);
        _companyClientMappingRepository.insert(expectedCompanyClientMapping);
    }

    @Test
    public void testInsertIfDoesNotExistWhenDoNot() throws Exception {
        insertFixture("company_client_mapping_repository_insert.sql");
        CompanyClientMapping companyClientMapping = _companyClientMappingRepository.insertIfDoesNotExist(1, 1);
        assertNotNull(companyClientMapping);
    }

    @Test
    public void testInsertIfDoesNotExistWhenDoes() throws Exception {
        insertFixture("company_client_mapping_repository_insert_if_does_not_exist_when_does.sql");
        CompanyClientMapping companyClientMapping = _companyClientMappingRepository.insertIfDoesNotExist(1, 1);
        assertNotNull(companyClientMapping);
        Assert.assertEquals(1, companyClientMapping.getCompanyClientMappingId());
        Assert.assertEquals(1, companyClientMapping.getClient().getClientId());
        Assert.assertEquals(1, companyClientMapping.getCompanyId());
    }

    @Test
    public void testUpdatePoints() throws Exception {
        insertFixture("company_client_mapping_repository_update.sql");
        CompanyClientMapping beforeUpdateCompanyClientMapping = getCompanyClientMappingById(1);
        assertEquals(10, beforeUpdateCompanyClientMapping.getPoints(), 0.00);
        beforeUpdateCompanyClientMapping.setPoints(20);
        int updatedRows = _companyClientMappingRepository.updatePoints(beforeUpdateCompanyClientMapping);
        CompanyClientMapping afterUpdateCompanyClientMapping = getCompanyClientMappingById(1);
        assertEquals(1, updatedRows);
        assertEquals(20, afterUpdateCompanyClientMapping.getPoints(), 0.00);
    }

    private CompanyClientMapping getCompanyClientMappingById(long companyClientMappingId) throws Exception {
        Statement st = null;
        CompanyClientMapping companyClientMapping = new CompanyClientMapping();
        try {
            st = getQueryAgent().getConnection().createStatement();
            ResultSet resultSet = st.executeQuery("SELECT * FROM company_client_mapping WHERE company_client_mapping_id = " + companyClientMappingId);
            if (resultSet.next()) {
                companyClientMapping.setCompanyClientMappingId(resultSet.getLong("company_client_mapping_id"));
                companyClientMapping.setCompanyId(resultSet.getLong("company_id"));
                companyClientMapping.setClient(buildClient(resultSet));
                companyClientMapping.setPoints(resultSet.getFloat("points"));
            }
        } finally {
            if (st != null) {
                st.close();
            }
        }
        return companyClientMapping;
    }

    private Client buildClient(ResultSet resultSet) throws SQLException {
        Client client = new Client();
        client.setClientId(resultSet.getLong("client_id"));
        return client;
    }

    private CompanyClientMappingRepository createCompanyClientMappingRepository(final QueryAgent queryAgent) {
        return new CompanyClientMappingRepository() {
            @Override
            protected QueryAgent getQueryAgent() throws Exception {
                return queryAgent;
            }
        };
    }
}