package com.neerpoints.api.controller;

import javax.servlet.annotation.MultipartConfig;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import com.neerpoints.model.PointsInCompany;
import com.neerpoints.service.CompanyService;
import com.neerpoints.service.model.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/points_in_company")
@MultipartConfig
public class PointsInCompanyController extends AbstractRestController {

    private CompanyService _companyService;

    @Autowired
    public PointsInCompanyController(CompanyService companyService) {
        _companyService = companyService;
    }

    @RequestMapping(value = "/{phone}", method = GET)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseEntity<ServiceResult<List<PointsInCompany>>> getByClientId(@PathVariable("phone") String phone) {
        ServiceResult<List<PointsInCompany>> serviceResult = _companyService.getPointsInCompanyByPhone(phone);
        return new ResponseEntity<>(serviceResult, HttpStatus.OK);
    }
}
