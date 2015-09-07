package com.lealpoints.controller.api;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import com.lealpoints.service.PointsService;
import com.lealpoints.service.model.PointsAwarding;
import com.lealpoints.service.model.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/points")
public class PointsController extends AbstractApiController {

    private PointsService _pointsService;

    @Autowired
    public PointsController(PointsService pointsService) {
        _pointsService = pointsService;
    }

    @RequestMapping(method = POST, headers = ACCEPT_HEADER)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseEntity<ServiceResult<Float>> awardPoints(@RequestBody PointsAwarding pointsAwarding) {
        try {
            long companyId = pointsAwarding.getCompanyId();
            pointsAwarding.setCompanyId(companyId);
            ServiceResult<Float> serviceResult = _pointsService.awardPoints(pointsAwarding);
            return new ResponseEntity<>(serviceResult, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ServiceResult<Float>(false, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
