package com.lealpoints.controller.api.v1;

import com.lealpoints.controller.base.BaseController;
import com.lealpoints.service.NotificationService;
import com.lealpoints.service.response.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.annotation.MultipartConfig;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController
@RequestMapping("/api/v1/notification")
@MultipartConfig
public class NotificationController extends BaseController {

    private final NotificationService _companyService;

    @Autowired
    public NotificationController(NotificationService companyService) {
        _companyService = companyService;
    }


    @RequestMapping(value = "/{companyId}/{phone}/send_promo_sms", method = PUT)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseEntity<ServiceResult> sendMobileAppAdMessage(@PathVariable("companyId") long companyId, @PathVariable("phone") String phone) {
        ServiceResult serviceResult = _companyService.sendMobileAppAdMessage(companyId, phone);
        return new ResponseEntity<>(serviceResult, HttpStatus.OK);
    }

}
