package com.monederobingo.controller;

import com.monederobingo.controller.base.BaseController;
import com.lealpoints.service.ClientUserService;
import com.lealpoints.service.model.ClientLoginResult;
import com.lealpoints.service.model.ClientUserLogin;
import com.lealpoints.service.model.ClientUserRegistration;
import com.lealpoints.service.response.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/client")
public class ClientAuthenticationController extends BaseController
{

    private final ClientUserService _clientUserService;

    @Autowired
    public ClientAuthenticationController(ClientUserService clientUserService) {
        _clientUserService = clientUserService;
    }

    @RequestMapping(method = POST, headers = ACCEPT_HEADER)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseEntity<ServiceResult<String>> register(@RequestBody ClientUserRegistration clientUserRegistration,
                                                          HttpServletRequest servletRequest) {
        ServiceResult<String> serviceResult = _clientUserService.register(clientUserRegistration);
        return new ResponseEntity<>(serviceResult, HttpStatus.OK);
    }

    @RequestMapping(value = "/login", method = POST, headers = ACCEPT_HEADER)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseEntity<ServiceResult<ClientLoginResult>> loginUser(@RequestBody ClientUserLogin clientUserLogin) throws Exception {
        ServiceResult<ClientLoginResult> serviceResult = _clientUserService.login(clientUserLogin);
        return new ResponseEntity<>(serviceResult, HttpStatus.OK);
    }

    @RequestMapping(value = "/resend_key", method = POST, headers = ACCEPT_HEADER)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseEntity<ServiceResult<Boolean>> resendKey(@RequestBody ClientUserRegistration clientUserRegistration) {
        ServiceResult<Boolean> serviceResult = _clientUserService.resendKey(clientUserRegistration.getPhoneNumber());
        return new ResponseEntity<>(serviceResult, HttpStatus.OK);
    }

}
