package com.lealpoints.controller.api.v1;

import com.lealpoints.controller.base.BaseController;
import com.lealpoints.service.CompanyUserService;
import com.lealpoints.service.model.CompanyUserRegistration;
import com.lealpoints.service.response.ServiceMessage;
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
import java.util.ArrayList;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/v1/company_users")
public class CompanyUserController extends BaseController {
    private CompanyUserService _companyUserService;

    @Autowired
    public CompanyUserController(CompanyUserService companyUserService) {
        _companyUserService = companyUserService;
    }

    @RequestMapping(value = "/register", method = POST, headers = ACCEPT_HEADER)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseEntity<ServiceResult> register(@RequestBody CompanyUserRegistration companyUserRegistration) throws Exception {
        ServiceResult serviceResult = _companyUserService.register(companyUserRegistration);
        return new ResponseEntity<>(serviceResult, HttpStatus.OK);
    }

    @RequestMapping(value = "/{companyId}", method = GET, headers = ACCEPT_HEADER)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseEntity<ServiceResult<List<String>>> get(@PathVariable("companyId") Long companyId) {
        List<String> namesList = new ArrayList<>();
        namesList.add("none");
        return new ResponseEntity<>(new ServiceResult<>(false, new ServiceMessage("Operation not supported yet"),
                namesList), HttpStatus.OK);
    }

}
