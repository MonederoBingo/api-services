package com.lealpoints.service.implementations;

import com.lealpoints.context.ThreadContextService;
import com.lealpoints.db.queryagent.QueryAgent;
import com.lealpoints.i18n.Message;
import com.lealpoints.model.Client;
import com.lealpoints.model.CompanyClientMapping;
import com.lealpoints.repository.ClientRepository;
import com.lealpoints.repository.CompanyClientMappingRepository;
import com.lealpoints.service.model.ClientRegistration;
import com.lealpoints.service.model.ValidationResult;
import com.lealpoints.service.response.ServiceMessage;
import com.lealpoints.service.response.ServiceResult;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class ClientServiceImplTest extends BaseServiceTest {
    @Test
    public void testRegister() throws Exception {
        //given
        Client client = new Client();
        client.setClientId(1);
        client.setPhone("");
        final ClientRepository clientRepository = createClientRepository(client.toJSONObject().toString());
        final QueryAgent queryAgent = createQueryAgent();
        final ThreadContextService threadContextService = createThreadContextService(queryAgent);
        final CompanyClientMappingRepository companyClientMappingRepository = createCompanyClientMappingRepositoryForInsert();
        PhoneValidatorServiceImpl phoneValidationService = createStrictMock(PhoneValidatorServiceImpl.class);
        expect(phoneValidationService.validate(anyString())).andReturn(new ValidationResult(true));
        replay(phoneValidationService);
        final ClientServiceImpl clientService =
                new ClientServiceImpl(clientRepository, companyClientMappingRepository, threadContextService, phoneValidationService) {

            @Override
            public ServiceMessage getServiceMessage(Message message, String... params) {
                return new ServiceMessage(message.name());
            }
        };
        ClientRegistration clientRegistration = new ClientRegistration();
        clientRegistration.setCompanyId(1);
        clientRegistration.setPhone("6141112233");

        //when
        ServiceResult serviceResult = clientService.register(clientRegistration);

        //then
        assertNotNull(serviceResult);
        assertTrue(serviceResult.isSuccess());
        assertEquals(1L, serviceResult.getObject());
        verify(clientRepository, companyClientMappingRepository, phoneValidationService);
    }

    @Test
    public void testRegisterWithInvalidPhone() {
        PhoneValidatorServiceImpl phoneValidatorService = createStrictMock(PhoneValidatorServiceImpl.class);
        expect(phoneValidatorService.validate(anyString()))
                .andReturn(new ValidationResult(false, new ServiceMessage(Message.PHONE_MUST_HAVE_10_DIGITS.name())));
        replay(phoneValidatorService);
        final ClientServiceImpl clientService = new ClientServiceImpl(null, null, null, phoneValidatorService) {
            @Override
            public ServiceMessage getServiceMessage(Message message, String... params) {
                return new ServiceMessage(message.name());
            }
        };
        ClientRegistration clientRegistration = new ClientRegistration();
        clientRegistration.setPhone("123");
        ServiceResult<Long> serviceResult = clientService.register(clientRegistration);
        assertNotNull(serviceResult);
        assertEquals(Message.PHONE_MUST_HAVE_10_DIGITS.name(), serviceResult.getMessage());
        verify(phoneValidatorService);
    }


    @Test
    public void testRegisterWhenThereIsAnExistentMapping() throws Exception {
        //given
        final ClientRepository clientRepository = createClientRepositoryWhenThereIsAnExistentMapping();
        final CompanyClientMappingRepository companyClientMappingRepository = createCompanyClientMappingRepositoryWhenThereIsAnExistentMapping();
        final PhoneValidatorServiceImpl phoneValidatorService = createNiceMock(PhoneValidatorServiceImpl.class);
        expect(phoneValidatorService.validate(anyString())).andReturn(new ValidationResult(true));
        replay(phoneValidatorService);
        final ClientServiceImpl clientService =
                new ClientServiceImpl(clientRepository, companyClientMappingRepository, null, phoneValidatorService) {
                    @Override
                    public ServiceMessage getServiceMessage(Message message, String... params) {
                        return new ServiceMessage(message.name());
                    }
        };

        final ClientRegistration clientRegistration = new ClientRegistration();
        clientRegistration.setPhone("1234567890");

        //when
        ServiceResult serviceResult = clientService.register(clientRegistration);

        //then
        assertNotNull(serviceResult);
        assertFalse(serviceResult.isSuccess());
        assertEquals(Message.THE_CLIENT_ALREADY_EXISTS.name(), serviceResult.getMessage());
        assertNull(serviceResult.getObject());
        verify(clientRepository, companyClientMappingRepository);
    }

    @Test
    public void testGetByCompanyId() throws Exception {
        final List<CompanyClientMapping> expectedClients = new ArrayList<>();
        expectedClients.add(createClient(100, "6391112233"));
        expectedClients.add(createClient(200, "6141112233"));
        CompanyClientMapping[] companyClientMappings = new CompanyClientMapping[expectedClients.size()];
        expectedClients.toArray(companyClientMappings);

        final ClientRepository clientRepository = createClientRepositoryForGet(new JSONArray(companyClientMappings).toString());
        final ClientServiceImpl clientService = new ClientServiceImpl(clientRepository, null, null, null);

        xyz.greatapp.libs.service.ServiceResult serviceResult = clientService.getByCompanyId(1);
        assertNotNull(serviceResult);
        assertTrue(serviceResult.isSuccess());
        assertEquals("", serviceResult.getMessage());
        assertNotNull(serviceResult.getObject());

        JSONArray actualClients = new JSONArray(serviceResult.getObject());
        assertEquals(2, actualClients.length());

        assertEquals(100, actualClients.getJSONObject(0).getDouble("points"), 0.00);
        JSONObject client = actualClients.getJSONObject(0).getJSONObject("client");
        assertEquals("6391112233", client.get("phone"));

        assertEquals(200, actualClients.getJSONObject(1).getDouble("points"), 0.00);
        client = actualClients.getJSONObject(1).getJSONObject("client");
        assertEquals("6141112233", client.get("phone"));

        verify(clientRepository);
    }

    @Test
    public void testGetByCompanyIdPhone() throws Exception {
        final CompanyClientMapping companyClientMapping = new CompanyClientMapping();
        companyClientMapping.setPoints(1200);
        Client client = new Client();
        client.setPhone("1234567890");
        companyClientMapping.setClient(client);
        final ClientRepository clientRepository = createClientRepositoryForGetByCompanyIDPhone(new JSONObject(companyClientMapping).toString());
        final ClientServiceImpl clientService = new ClientServiceImpl(clientRepository, null, null, null);

        xyz.greatapp.libs.service.ServiceResult serviceResult = clientService.getByCompanyIdPhone(1, "1234567890");
        assertNotNull(serviceResult);
        assertTrue(serviceResult.isSuccess());
        assertEquals("", serviceResult.getMessage());
        assertNotNull(serviceResult.getObject());

        final JSONObject actualCompanyClientMapping = new JSONObject(serviceResult.getObject());
        assertEquals(1200, actualCompanyClientMapping.getDouble("points"), 0.00);

        assertEquals("1234567890", new JSONObject(actualCompanyClientMapping.get("client").toString()).get("phone"));
        verify(clientRepository);
    }

    private ThreadContextService createThreadContextService(QueryAgent queryAgent) throws SQLException {
        ThreadContextService threadContextService = createMock(ThreadContextService.class);

        expect(threadContextService.getQueryAgent()).andReturn(queryAgent).times(2);
        replay(threadContextService);
        return threadContextService;
    }

    private CompanyClientMappingRepository createCompanyClientMappingRepositoryForInsert() throws Exception {
        CompanyClientMappingRepository companyClientMappingRepository = createMock(CompanyClientMappingRepository.class);
        expect(companyClientMappingRepository.insert((CompanyClientMapping) anyObject())).andReturn(1L);
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

    private ClientRepository createClientRepository(String client) throws Exception {
        ClientRepository clientRepository = createMock(ClientRepository.class);
        expect(clientRepository.insertIfDoesNotExist(anyString(), anyBoolean()))
                .andReturn(new xyz.greatapp.libs.service.ServiceResult(true, "", client)).anyTimes();
        expect(clientRepository.getByPhone(anyString())).andReturn(new xyz.greatapp.libs.service.ServiceResult(true, "", client)).anyTimes();
        replay(clientRepository);
        return clientRepository;
    }

    private ClientRepository createClientRepositoryWhenThereIsAnExistentMapping() throws Exception {
        ClientRepository clientRepository = createMock(ClientRepository.class);
        Client client = new Client();
        client.setPhone("");
        expect(clientRepository.getByPhone(anyString()))
                .andReturn(new xyz.greatapp.libs.service.ServiceResult(true, "", client.toJSONObject().toString())).anyTimes();
        replay(clientRepository);
        return clientRepository;
    }

    private ClientRepository createClientRepositoryForGet(String object) throws Exception {
        ClientRepository clientRepository = createMock(ClientRepository.class);
        xyz.greatapp.libs.service.ServiceResult serviceResult =
                new xyz.greatapp.libs.service.ServiceResult(true, "", object);
        expect(clientRepository.getByCompanyId(anyLong())).andReturn(serviceResult).anyTimes();
        replay(clientRepository);
        return clientRepository;
    }

    private ClientRepository createClientRepositoryForGetByCompanyIDPhone(String companyClientMapping) throws Exception {
        ClientRepository clientRepository = createMock(ClientRepository.class);
        expect(clientRepository.getByCompanyIdPhone(anyLong(), anyString())).
                andReturn(new xyz.greatapp.libs.service.ServiceResult(true, "", companyClientMapping)).anyTimes();
        replay(clientRepository);
        return clientRepository;
    }
}