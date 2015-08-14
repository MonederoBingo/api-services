package com.lealpoints.controller.api;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import com.lealpoints.model.PointsConfiguration;
import com.lealpoints.service.PointsConfigurationService;
import com.lealpoints.service.model.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController
@RequestMapping("/points_configuration")
public class PointsConfigurationController extends AbstractApiController {

    private PointsConfigurationService _pointsConfigurationService;

    @Autowired
    public PointsConfigurationController(PointsConfigurationService pointsConfigurationService) {
        _pointsConfigurationService = pointsConfigurationService;
    }

    @RequestMapping(value = "/{companyId}", method = GET, headers = ACCEPT_HEADER)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseEntity<ServiceResult<PointsConfiguration>> get(@PathVariable("companyId") long companyId) {
        try {
            ServiceResult<PointsConfiguration> serviceResult = _pointsConfigurationService.getByCompanyId(companyId);
            return new ResponseEntity<>(serviceResult, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ServiceResult<PointsConfiguration>(false, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(method = PUT, headers = ACCEPT_HEADER)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseEntity<ServiceResult<Boolean>> update(@RequestBody PointsConfiguration pointsConfiguration) {
        try {
            pointsConfiguration.setCompanyId(pointsConfiguration.getCompanyId());
            ServiceResult<Boolean> serviceResult = _pointsConfigurationService.update(pointsConfiguration);
            return new ResponseEntity<>(serviceResult, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ServiceResult<Boolean>(false, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
