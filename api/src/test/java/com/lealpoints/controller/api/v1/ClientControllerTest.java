package com.lealpoints.controller.api.v1;

import com.lealpoints.model.Client;
import com.lealpoints.model.CompanyClientMapping;
import com.lealpoints.service.implementations.ClientServiceImpl;
import com.lealpoints.service.model.ClientRegistration;
import com.lealpoints.service.response.ServiceMessage;
import com.lealpoints.service.response.ServiceResult;
import org.easymock.EasyMock;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ClientControllerTest {
    @Test
    public void testRegisterClient() throws Exception {
        final ServiceResult<Long> expectedServiceResult = new ServiceResult<>(true, new ServiceMessage("1"));
        final ClientServiceImpl clientService = createClientServiceForRegister(expectedServiceResult);
        final ClientController clientController = new ClientController(clientService);

        ClientRegistration clientRegistration = new ClientRegistration();
        clientRegistration.setPhone("6141112233");

        ResponseEntity<ServiceResult> responseEntity = clientController.register(clientRegistration);
        assertNotNull(responseEntity);
        ServiceResult actualServiceResults = responseEntity.getBody();
        assertNotNull(actualServiceResults);
        assertEquals(expectedServiceResult.isSuccess(), actualServiceResults.isSuccess());
        assertEquals(expectedServiceResult.getMessage(), actualServiceResults.getMessage());
        verify(clientService);
    }

    @Test
    public void testGetByCompanyId() throws Exception {
        List<CompanyClientMapping> expectedClients = new ArrayList<>();
        expectedClients.add(createCompanyClientMapping(100, "123"));
        expectedClients.add(createCompanyClientMapping(200, "456"));
        final ServiceResult<List<CompanyClientMapping>> expectedServiceResult = new ServiceResult<>(true, new ServiceMessage("1"), expectedClients);
        final ClientServiceImpl clientService = createClientServiceForGet(expectedServiceResult);
        final ClientController clientController = new ClientController(clientService);

        ResponseEntity<ServiceResult<List<CompanyClientMapping>>> responseEntity = clientController.getByCompanyId(1);
        assertNotNull(responseEntity);
        ServiceResult actualServiceResults = responseEntity.getBody();
        assertNotNull(actualServiceResults);
        assertEquals(expectedServiceResult.isSuccess(), actualServiceResults.isSuccess());
        assertEquals(expectedServiceResult.getMessage(), actualServiceResults.getMessage());
        assertNotNull(expectedServiceResult.getObject());
        List<CompanyClientMapping> companyClientMappings = expectedServiceResult.getObject();
        assertEquals(2, companyClientMappings.size());
        assertEquals(100, companyClientMappings.get(0).getPoints(), 0.00);
        assertEquals("123", companyClientMappings.get(0).getClient().getPhone());
        assertEquals(200, companyClientMappings.get(1).getPoints(), 0.00);
        assertEquals("456", companyClientMappings.get(1).getClient().getPhone());
    }

    @Test
    public void testGetByCompanyIdPhone() throws Exception {
        CompanyClientMapping companyClientMapping = new CompanyClientMapping();
        companyClientMapping.setPoints(1200);
        Client client = new Client();
        client.setPhone("1234567890");
        companyClientMapping.setClient(client);
        final ServiceResult<CompanyClientMapping> expectedServiceResult = new ServiceResult<>(true, new ServiceMessage("1"), companyClientMapping);
        final ClientServiceImpl clientService = createClientServiceForGetByCompanyIdPhone(expectedServiceResult);
        final ClientController clientController = new ClientController(clientService);

        ResponseEntity<ServiceResult<CompanyClientMapping>> responseEntity = clientController.getByCompanyIdPhone(1, "1234567890");
        assertNotNull(responseEntity);
        ServiceResult actualServiceResults = responseEntity.getBody();
        assertNotNull(actualServiceResults);
        assertEquals(expectedServiceResult.isSuccess(), actualServiceResults.isSuccess());
        assertEquals(expectedServiceResult.getMessage(), actualServiceResults.getMessage());
        assertNotNull(expectedServiceResult.getObject());
        CompanyClientMapping actualCompanyClientMapping = expectedServiceResult.getObject();
        assertEquals(1200, actualCompanyClientMapping.getPoints(), 0.00);
        assertEquals("1234567890", actualCompanyClientMapping.getClient().getPhone());
    }

    private CompanyClientMapping createCompanyClientMapping(float points, String phone) {
        CompanyClientMapping companyClientMapping = new CompanyClientMapping();
        companyClientMapping.setPoints(points);
        Client client = new Client();
        client.setPhone(phone);
        companyClientMapping.setClient(client);
        return companyClientMapping;
    }

    private ClientServiceImpl createClientServiceForRegister(ServiceResult<Long> serviceResult) throws Exception {
        final ClientServiceImpl clientService = EasyMock.createMock(ClientServiceImpl.class);
        expect(clientService.register((ClientRegistration) anyObject())).andReturn(serviceResult).times(1);
        replay(clientService);
        return clientService;
    }

    private ClientServiceImpl createClientServiceForGet(ServiceResult<List<CompanyClientMapping>> serviceResult) throws Exception {
        final ClientServiceImpl clientService = EasyMock.createMock(ClientServiceImpl.class);
        expect(clientService.getByCompanyId(anyLong())).andReturn(serviceResult);
        replay(clientService);
        return clientService;
    }

    private ClientServiceImpl createClientServiceForGetByCompanyIdPhone(ServiceResult<CompanyClientMapping> serviceResult) throws Exception {
        final ClientServiceImpl clientService = EasyMock.createMock(ClientServiceImpl.class);
        expect(clientService.getByCompanyIdPhone(anyLong(), anyString())).andReturn(serviceResult);
        replay(clientService);
        return clientService;
    }
}