package com.lealpoints.controller.api.v1;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import com.lealpoints.controller.base.BaseController;
import com.lealpoints.model.CompanyClientMapping;
import com.lealpoints.service.ClientService;
import com.lealpoints.service.model.ClientRegistration;
import com.lealpoints.service.model.ServiceResult;
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
@RequestMapping("/v1/clients")
public class ClientController extends BaseController {

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
    public ResponseEntity<ServiceResult<List<CompanyClientMapping>>> getByCompanyId(@PathVariable("companyId") long companyId) {
        ServiceResult<List<CompanyClientMapping>> serviceResult = _clientService.getByCompanyId(companyId);
        return new ResponseEntity<>(serviceResult, HttpStatus.OK);
    }

    @RequestMapping(value = "/{companyId}/{phone}", method = GET)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseEntity<ServiceResult<CompanyClientMapping>> getByCompanyIdPhone(@PathVariable("companyId") long companyId,
        @PathVariable("phone") String phone) {
        ServiceResult<CompanyClientMapping> serviceResult = _clientService.getByCompanyIdPhone(companyId, phone);
        return new ResponseEntity<>(serviceResult, HttpStatus.OK);
    }
}
