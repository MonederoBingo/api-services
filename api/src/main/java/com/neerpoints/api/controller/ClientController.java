package com.neerpoints.api.controller;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import com.neerpoints.model.ClientPoints;
import com.neerpoints.service.ClientService;
import com.neerpoints.service.model.ClientRegistration;
import com.neerpoints.service.model.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/clients")
public class ClientController extends AbstractRestController {

    private ClientService _clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        _clientService = clientService;
    }

    @RequestMapping(method = POST, headers = ACCEPT_HEADER)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseEntity<ServiceResult> register(@RequestBody ClientRegistration clientRegistration) {
        try {
            ServiceResult serviceResult = _clientService.register(clientRegistration);
            return new ResponseEntity<>(serviceResult, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ServiceResult(false, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/{companyId}", method = GET)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseEntity<ServiceResult<List<ClientPoints>>> getByCompanyId(@PathVariable("companyId") long companyId) {
        ServiceResult<List<ClientPoints>> serviceResult = _clientService.getByCompanyId(companyId);
        return new ResponseEntity<>(serviceResult, HttpStatus.OK);
    }

    @RequestMapping(value = "/{companyId}/{phone}", method = GET)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseEntity<ServiceResult<ClientPoints>> getByCompanyIdPhone(@PathVariable("companyId") long companyId,
        @PathVariable("phone") String phone) {
        ServiceResult<ClientPoints> serviceResult = _clientService.getByCompanyIdPhone(companyId, phone);
        return new ResponseEntity<>(serviceResult, HttpStatus.OK);
    }
}
