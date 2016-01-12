package com.lealpoints.controller.api.v1;

import com.lealpoints.controller.base.BaseController;
import com.lealpoints.model.PointsInCompany;
import com.lealpoints.service.CompanyService;
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
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/api/v1/points_in_company")
@MultipartConfig
public class PointsInCompanyController extends BaseController {

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
