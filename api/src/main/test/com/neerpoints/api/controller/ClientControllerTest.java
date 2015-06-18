package com.neerpoints.api.controller;

import java.util.ArrayList;
import java.util.List;
import com.neerpoints.model.ClientPoints;
import com.neerpoints.service.ClientService;
import com.neerpoints.service.model.ClientRegistration;
import com.neerpoints.service.model.ServiceResult;
import org.easymock.EasyMock;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ClientControllerTest {
    @Test
    public void testRegisterClient() throws Exception {
        final ServiceResult<Long> expectedServiceResult = new ServiceResult<>(true, "1");
        final ClientService clientService = createClientServiceForRegister(expectedServiceResult);
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
        List<ClientPoints> expectedClients = new ArrayList<>();
        expectedClients.add(createClientPoints(100, "123"));
        expectedClients.add(createClientPoints(200, "456"));
        final ServiceResult<List<ClientPoints>> expectedServiceResult = new ServiceResult<>(true, "1", expectedClients);
        final ClientService clientService = createClientServiceForGet(expectedServiceResult);
        final ClientController clientController = new ClientController(clientService);

        ResponseEntity<ServiceResult<List<ClientPoints>>> responseEntity = clientController.getByCompanyId(1);
        assertNotNull(responseEntity);
        ServiceResult actualServiceResults = responseEntity.getBody();
        assertNotNull(actualServiceResults);
        assertEquals(expectedServiceResult.isSuccess(), actualServiceResults.isSuccess());
        assertEquals(expectedServiceResult.getMessage(), actualServiceResults.getMessage());
        assertNotNull(expectedServiceResult.getObject());
        List<ClientPoints> actualClients = expectedServiceResult.getObject();
        assertEquals(2, actualClients.size());
        assertEquals(100, actualClients.get(0).getPoints(), 0.00);
        assertEquals("123", actualClients.get(0).getPhone());
        assertEquals(200, actualClients.get(1).getPoints(), 0.00);
        assertEquals("456", actualClients.get(1).getPhone());
    }

    @Test
    public void testGetByCompanyIdPhone() throws Exception {
        ClientPoints clientPoints = new ClientPoints();
        clientPoints.setPoints(1200);
        clientPoints.setPhone("1234567890");
        final ServiceResult<ClientPoints> expectedServiceResult = new ServiceResult<>(true, "1", clientPoints);
        final ClientService clientService = createClientServiceForGetByCompanyIdPhone(expectedServiceResult);
        final ClientController clientController = new ClientController(clientService);

        ResponseEntity<ServiceResult<ClientPoints>> responseEntity = clientController.getByCompanyIdPhone(1, "1234567890");
        assertNotNull(responseEntity);
        ServiceResult actualServiceResults = responseEntity.getBody();
        assertNotNull(actualServiceResults);
        assertEquals(expectedServiceResult.isSuccess(), actualServiceResults.isSuccess());
        assertEquals(expectedServiceResult.getMessage(), actualServiceResults.getMessage());
        assertNotNull(expectedServiceResult.getObject());
        ClientPoints actualClientPoints = expectedServiceResult.getObject();
        assertEquals(1200, actualClientPoints.getPoints(), 0.00);
        assertEquals("1234567890", actualClientPoints.getPhone());
    }

    private ClientPoints createClientPoints(float points, String phone) {
        ClientPoints clientPoints = new ClientPoints();
        clientPoints.setPoints(points);
        clientPoints.setPhone(phone);
        return clientPoints;
    }

    private ClientService createClientServiceForRegister(ServiceResult<Long> serviceResult) throws Exception {
        final ClientService clientService = EasyMock.createMock(ClientService.class);
        expect(clientService.register((ClientRegistration) anyObject())).andReturn(serviceResult).times(1);
        replay(clientService);
        return clientService;
    }

    private ClientService createClientServiceForGet(ServiceResult<List<ClientPoints>> serviceResult) throws Exception {
        final ClientService clientService = EasyMock.createMock(ClientService.class);
        expect(clientService.getByCompanyId(anyLong())).andReturn(serviceResult);
        replay(clientService);
        return clientService;
    }

    private ClientService createClientServiceForGetByCompanyIdPhone(ServiceResult<ClientPoints> serviceResult) throws Exception {
        final ClientService clientService = EasyMock.createMock(ClientService.class);
        expect(clientService.getByCompanyIdPhone(anyLong(), anyString())).andReturn(serviceResult);
        replay(clientService);
        return clientService;
    }
}