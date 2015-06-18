package com.neerpoints.api.controller;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import com.neerpoints.service.CompanyUserService;
import com.neerpoints.service.model.CompanyUserLogin;
import com.neerpoints.service.model.CompanyUserPasswordChanging;
import com.neerpoints.service.model.LoginResult;
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
@RequestMapping("/company_users")
public class CompanyUserController extends AbstractRestController {

    private final CompanyUserService _companyUserService;

    @Autowired
    public CompanyUserController(CompanyUserService companyUserService) {
        _companyUserService = companyUserService;
    }

    @RequestMapping(value="/login", method = POST, headers = ACCEPT_HEADER)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseEntity<ServiceResult<LoginResult>> login(@RequestBody CompanyUserLogin companyUserLogin) {
        try {
            ServiceResult<LoginResult> serviceResult = _companyUserService.loginUser(companyUserLogin);
            return new ResponseEntity<>(serviceResult, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ServiceResult<LoginResult>(false, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/send_activation_email/{email}", method = POST, headers = ACCEPT_HEADER)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseEntity<ServiceResult> sendActivationEmail(@PathVariable("email") String email) {
        try {
            ServiceResult serviceResult = _companyUserService.sendActivationEmail(email);
            return new ResponseEntity<>(serviceResult, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ServiceResult(false, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/activate/{activationKey}", method = GET, headers = ACCEPT_HEADER)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseEntity<ServiceResult> activate(@PathVariable("activationKey") String activationKey) {
        try {
            ServiceResult serviceResult = _companyUserService.activateUser(activationKey);
            return new ResponseEntity<>(serviceResult, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ServiceResult(false, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/send_temp_password_email", method = POST, headers = ACCEPT_HEADER)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseEntity<ServiceResult> sendTempPasswordEmail(@RequestBody String email) {
        try {
            ServiceResult serviceResult = _companyUserService.sendTempPasswordEmail(email);
            return new ResponseEntity<>(serviceResult, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ServiceResult(false, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/change_password", method = POST, headers = ACCEPT_HEADER)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseEntity<ServiceResult> changePassword(@RequestBody CompanyUserPasswordChanging passwordChanging) {
        try {
            ServiceResult serviceResult = _companyUserService.changePassword(passwordChanging);
            return new ResponseEntity<>(serviceResult, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ServiceResult(false, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
