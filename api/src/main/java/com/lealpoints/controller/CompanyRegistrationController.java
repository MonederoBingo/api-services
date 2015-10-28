package com.lealpoints.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import com.lealpoints.controller.base.BaseController;
import com.lealpoints.service.CompanyService;
import com.lealpoints.service.CompanyUserService;
import com.lealpoints.service.model.CompanyLoginResult;
import com.lealpoints.service.model.CompanyRegistration;
import com.lealpoints.service.model.CompanyUserLogin;
import com.lealpoints.service.model.CompanyUserPasswordChanging;
import com.lealpoints.service.model.ServiceResult;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/company")
public class CompanyRegistrationController extends BaseController {

    private final CompanyUserService _companyUserService;
    private final CompanyService _companyService;

    @Autowired
    public CompanyRegistrationController(CompanyUserService companyUserService, CompanyService companyService) {
        _companyUserService = companyUserService;
        _companyService = companyService;
    }

    @RequestMapping(value="/register", method = POST, headers = ACCEPT_HEADER)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseEntity<ServiceResult> register(@RequestBody CompanyRegistration companyRegistration) {
        try {
            ServiceResult serviceResult = _companyService.register(companyRegistration);
            return new ResponseEntity<>(serviceResult, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ServiceResult(false, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @RequestMapping(value="/login", method = POST, headers = ACCEPT_HEADER)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseEntity<ServiceResult<CompanyLoginResult>> login(@RequestBody CompanyUserLogin companyUserLogin) {
        try {
            ServiceResult<CompanyLoginResult> serviceResult = _companyUserService.loginUser(companyUserLogin);
            return new ResponseEntity<>(serviceResult, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ServiceResult<CompanyLoginResult>(false, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/send_activation_email", method = POST, headers = ACCEPT_HEADER)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseEntity<ServiceResult> sendActivationEmail(@RequestBody String email) {
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

    @RequestMapping(value = "/logo/{companyId}", method = GET)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseEntity<byte[]> getLogo(@PathVariable("companyId") long companyId, HttpServletRequest request, HttpServletResponse response) {
        try {
            File file = _companyService.getLogo(companyId);
            InputStream input = new FileInputStream(file);
            final HttpHeaders headers = new HttpHeaders();
            headers.setContentType(getMediaTypeFromExtension(FilenameUtils.getExtension(file.getName())));
            return new ResponseEntity<>(IOUtils.toByteArray(input), headers, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private org.springframework.http.MediaType getMediaTypeFromExtension(String extension) {
        if (extension.equalsIgnoreCase("png")) {
            return org.springframework.http.MediaType.IMAGE_PNG;
        } else if (extension.equalsIgnoreCase("gif")) {
            return org.springframework.http.MediaType.IMAGE_GIF;
        } else {
            return org.springframework.http.MediaType.IMAGE_PNG;
        }
    }
}
