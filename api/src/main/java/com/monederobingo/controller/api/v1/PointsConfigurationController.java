package com.monederobingo.controller.api.v1;

import com.monederobingo.controller.base.BaseController;
import com.lealpoints.model.PointsConfiguration;
import com.lealpoints.service.PointsConfigurationService;
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

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController
@RequestMapping("/api/v1/points_configuration")
public class PointsConfigurationController extends BaseController {

    private PointsConfigurationService _pointsConfigurationService;

    @Autowired
    public PointsConfigurationController(PointsConfigurationService pointsConfigurationService) {
        _pointsConfigurationService = pointsConfigurationService;
    }

    @RequestMapping(value = "/{companyId}", method = GET, headers = ACCEPT_HEADER)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseEntity<ServiceResult<PointsConfiguration>> get(@PathVariable("companyId") long companyId) {
        ServiceResult<PointsConfiguration> serviceResult = _pointsConfigurationService.getByCompanyId(companyId);
        return new ResponseEntity<>(serviceResult, HttpStatus.OK);
    }

    @RequestMapping(method = PUT, headers = ACCEPT_HEADER)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseEntity<ServiceResult<Boolean>> update(@RequestBody PointsConfiguration pointsConfiguration) {
        ServiceResult<Boolean> serviceResult = _pointsConfigurationService.update(pointsConfiguration);
        return new ResponseEntity<>(serviceResult, HttpStatus.OK);
    }
}
