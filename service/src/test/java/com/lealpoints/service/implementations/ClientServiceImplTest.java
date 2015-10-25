package com.lealpoints.service.implementations;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.lealpoints.context.ThreadContextService;
import com.lealpoints.db.queryagent.QueryAgent;
import com.lealpoints.model.Client;
import com.lealpoints.model.CompanyClientMapping;
import com.lealpoints.repository.ClientRepository;
import com.lealpoints.repository.CompanyClientMappingRepository;
import com.lealpoints.service.model.ClientRegistration;
import com.lealpoints.service.model.ServiceResult;
import com.lealpoints.service.model.ValidationResult;
import com.lealpoints.util.Translations;
import org.junit.Test;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class ClientServiceImplTest {
    @Test
    public void testRegister() throws Exception {
        Client client = new Client();
        client.setClientId(1);
        final ClientRepository clientRepository = createClientRepository(client);
        final QueryAgent queryAgent = createQueryAgent();
        final ThreadContextService threadContextService = createThreadContextService(queryAgent);
        final CompanyClientMappingRepository companyClientMappingRepository = createCompanyClientMappingRepositoryForInsert();
        PhoneValidatorServiceImpl phoneValidationService = createStrictMock(PhoneValidatorServiceImpl.class);
        expect(phoneValidationService.validate(anyString())).andReturn(new ValidationResult(true));
        replay(phoneValidationService);
        final ClientServiceImpl clientService =
            new ClientServiceImpl(clientRepository, companyClientMappingRepository, threadContextService, null, phoneValidationService) {

            @Override
            public String getTranslation(Translations.Message message) {
                return message.name();
            }
        };
        ClientRegistration clientRegistration = new ClientRegistration();
        clientRegistration.setCompanyId(1);
        clientRegistration.setPhone("6141112233");
        ServiceResult serviceResult = clientService.register(clientRegistration);
        assertNotNull(serviceResult);
        assertTrue(serviceResult.isSuccess());
        assertEquals(1l, serviceResult.getObject());
        verify(clientRepository, companyClientMappingRepository, phoneValidationService);
    }

    @Test
    public void testRegisterWithInvalidPhone() {
        PhoneValidatorServiceImpl phoneValidatorService = createStrictMock(PhoneValidatorServiceImpl.class);
        expect(phoneValidatorService.validate(anyString()))
            .andReturn(new ValidationResult(false, Translations.Message.PHONE_MUST_HAVE_10_DIGITS.name()));
        replay(phoneValidatorService);
        final ClientServiceImpl clientService = new ClientServiceImpl(null, null, null, null, phoneValidatorService) {
            @Override
            public String getTranslation(Translations.Message message) {
                return message.name();
            }
        };
        ClientRegistration clientRegistration = new ClientRegistration();
        clientRegistration.setPhone("123");
        ServiceResult<Long> serviceResult = clientService.register(clientRegistration);
        assertNotNull(serviceResult);
        assertEquals(Translations.Message.PHONE_MUST_HAVE_10_DIGITS.name(), serviceResult.getMessage());
        verify(phoneValidatorService);
    }


    @Test
    public void testRegisterWhenThereIsAnExistentMapping() throws Exception {
        final ClientRepository clientRepository = createClientRepositoryWhenThereIsAnExistentMapping();
        final CompanyClientMappingRepository companyClientMappingRepository = createCompanyClientMappingRepositoryWhenThereIsAnExistentMapping();
        final PhoneValidatorServiceImpl phoneValidatorService = createNiceMock(PhoneValidatorServiceImpl.class);
        expect(phoneValidatorService.validate(anyString())).andReturn(new ValidationResult(true));
        replay(phoneValidatorService);
        final ClientServiceImpl clientService =
            new ClientServiceImpl(clientRepository, companyClientMappingRepository, null, null, phoneValidatorService) {
            @Override
            public String getTranslation(Translations.Message message) {
                return message.name();
            }
        };

        final ClientRegistration clientRegistration = new ClientRegistration();
        clientRegistration.setPhone("1234567890");
        ServiceResult serviceResult = clientService.register(clientRegistration);
        assertNotNull(serviceResult);
        assertFalse(serviceResult.isSuccess());
        assertEquals(Translations.Message.THE_CLIENT_ALREADY_EXISTS.name(), serviceResult.getMessage());
        assertNull(serviceResult.getObject());
        verify(clientRepository, companyClientMappingRepository);
    }

    @Test
    public void testGetByCompanyId() throws Exception {
        final List<CompanyClientMapping> expectedClients = new ArrayList<>();
        expectedClients.add(createClient(100, "6391112233"));
        expectedClients.add(createClient(200, "6141112233"));
        final ClientRepository clientRepository = createClientRepositoryForGet(expectedClients);
        final ClientServiceImpl clientService = new ClientServiceImpl(clientRepository, null, null, null, null);

        ServiceResult<List<CompanyClientMapping>> serviceResult = clientService.getByCompanyId(1);
        assertNotNull(serviceResult);
        assertTrue(serviceResult.isSuccess());
        assertEquals("", serviceResult.getMessage());
        assertNotNull(serviceResult.getObject());

        List<CompanyClientMapping> actualClients = serviceResult.getObject();
        assertEquals(2, actualClients.size());
        assertEquals(100, actualClients.get(0).getPoints(), 0.00);
        assertEquals("6391112233", actualClients.get(0).getClient().getPhone());
        assertEquals(200, actualClients.get(1).getPoints(), 0.00);
        assertEquals("6141112233", actualClients.get(1).getClient().getPhone());
        verify(clientRepository);
    }

    @Test
    public void testGetByCompanyIdPhone() throws Exception {
        final CompanyClientMapping companyClientMapping = new CompanyClientMapping();
        companyClientMapping.setPoints(1200);
        Client client = new Client();
        client.setPhone("1234567890");
        companyClientMapping.setClient(client);
        final ClientRepository clientRepository = createClientRepositoryForGetByCompanyIDPhone(companyClientMapping);
        final ClientServiceImpl clientService = new ClientServiceImpl(clientRepository, null, null, null, null);

        ServiceResult<CompanyClientMapping> serviceResult = clientService.getByCompanyIdPhone(1, "1234567890");
        assertNotNull(serviceResult);
        assertTrue(serviceResult.isSuccess());
        assertEquals("", serviceResult.getMessage());
        assertNotNull(serviceResult.getObject());

        final CompanyClientMapping actualCompanyClientMapping = serviceResult.getObject();
        assertEquals(1200, actualCompanyClientMapping.getPoints(), 0.00);
        assertEquals("1234567890", actualCompanyClientMapping.getClient().getPhone());
        verify(clientRepository);
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

    private CompanyClientMapping createClient(float points, String phone) {
        CompanyClientMapping companyClientMapping = new CompanyClientMapping();
        companyClientMapping.setPoints(points);
        Client client = new Client();
        client.setPhone(phone);
        companyClientMapping.setClient(client);
        return companyClientMapping;
    }

    private CompanyClientMappingRepository createCompanyClientMappingRepositoryForInsert() throws Exception {
        CompanyClientMappingRepository companyClientMappingRepository = createMock(CompanyClientMappingRepository.class);
        expect(companyClientMappingRepository.insert((CompanyClientMapping) anyObject())).andReturn(1l);
        expect(companyClientMappingRepository.getByCompanyIdClientId(anyLong(), anyLong())).andReturn(null);
        replay(companyClientMappingRepository);
        return companyClientMappingRepository;
    }

    private CompanyClientMappingRepository createCompanyClientMappingRepositoryWhenThereIsAnExistentMapping() throws Exception {
        CompanyClientMappingRepository companyClientMappingRepository = createMock(CompanyClientMappingRepository.class);
        expect(companyClientMappingRepository.getByCompanyIdClientId(anyLong(), anyLong())).andReturn(new CompanyClientMapping());
        replay(companyClientMappingRepository);
        return companyClientMappingRepository;
    }

    private ClientRepository createClientRepository(Client client) throws Exception {
        ClientRepository clientRepository = createMock(ClientRepository.class);
        expect(clientRepository.insertIfDoesNotExist(anyString(), anyBoolean())).andReturn(client).anyTimes();
        expect(clientRepository.getByPhone(anyString())).andReturn(client).anyTimes();
        replay(clientRepository);
        return clientRepository;
    }

    private ClientRepository createClientRepositoryWhenThereIsAnExistentMapping() throws Exception {
        ClientRepository clientRepository = createMock(ClientRepository.class);
        expect(clientRepository.getByPhone(anyString())).andReturn(new Client()).anyTimes();
        replay(clientRepository);
        return clientRepository;
    }

    private ClientRepository createClientRepositoryForGet(List<CompanyClientMapping> companyClientMappingList) throws Exception {
        ClientRepository clientRepository = createMock(ClientRepository.class);
        expect(clientRepository.getByCompanyId(anyLong())).andReturn(companyClientMappingList).anyTimes();
        replay(clientRepository);
        return clientRepository;
    }

    private ClientRepository createClientRepositoryForGetByCompanyIDPhone(CompanyClientMapping companyClientMapping) throws Exception {
        ClientRepository clientRepository = createMock(ClientRepository.class);
        expect(clientRepository.getByCompanyIdPhone(anyLong(), anyString())).andReturn(companyClientMapping).anyTimes();
        replay(clientRepository);
        return clientRepository;
    }
}