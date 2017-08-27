package com.monederobingo.controller.api.v1;

import com.monederobingo.controller.base.BaseController;
import com.lealpoints.model.CompanyClientMapping;
import com.lealpoints.service.ClientService;
import com.lealpoints.service.model.ClientRegistration;
import com.lealpoints.service.response.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/api/v1/clients")
public class ClientController extends BaseController {

    private ClientService _clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        _clientService = clientService;
    }

    @RequestMapping(method = POST, headers = ACCEPT_HEADER)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseEntity<ServiceResult> register(@RequestBody ClientRegistration clientRegistration) {
        ServiceResult serviceResult = _clientService.register(clientRegistration);
        return new ResponseEntity<>(serviceResult, HttpStatus.OK);
    }

    @RequestMapping(value = "/{companyId}", method = GET)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseEntity<xyz.greatapp.libs.service.ServiceResult> getByCompanyId(@PathVariable("companyId") long companyId) {
        xyz.greatapp.libs.service.ServiceResult serviceResult = _clientService.getByCompanyId(companyId);
        return new ResponseEntity<>(serviceResult, HttpStatus.OK);
    }

    @RequestMapping(value = "/{companyId}/{phone}", method = GET)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseEntity<xyz.greatapp.libs.service.ServiceResult > getByCompanyIdPhone(@PathVariable("companyId") long companyId,
                                                                                   @PathVariable("phone") String phone) {
        xyz.greatapp.libs.service.ServiceResult serviceResult = _clientService.getByCompanyIdPhone(companyId, phone);
        return new ResponseEntity<>(serviceResult, HttpStatus.OK);
    }
}
