package com.lealpoints.controller.api.v1;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import com.lealpoints.controller.base.BaseController;
import com.lealpoints.model.PromotionConfiguration;
import com.lealpoints.service.PromotionConfigurationService;
import com.lealpoints.service.model.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@RequestMapping("/v1/promotion_configuration")
public class PromotionConfigurationController extends BaseController {

    private final PromotionConfigurationService _promotionConfigurationService;

    @Autowired
    public PromotionConfigurationController(PromotionConfigurationService promotionConfigurationService) {
        _promotionConfigurationService = promotionConfigurationService;
    }

    @RequestMapping(value = "/{companyId}", method = GET, headers = ACCEPT_HEADER)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseEntity<ServiceResult<List<PromotionConfiguration>>> get(@PathVariable("companyId") long companyId) {
        try {
            ServiceResult<List<PromotionConfiguration>> serviceResult = _promotionConfigurationService.getByCompanyId(companyId);
            return new ResponseEntity<>(serviceResult, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ServiceResult<List<PromotionConfiguration>>(false, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/{companyId}/{phone}", method = GET, headers = ACCEPT_HEADER)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseEntity<ServiceResult<List<PromotionConfiguration>>> getAvailableByPhone(@PathVariable("companyId") long companyId,
        @PathVariable("phone") String phone) {
        try {
            ServiceResult<List<PromotionConfiguration>> serviceResult = _promotionConfigurationService.getByCompanyIdRequiredPoints(companyId, phone);
            return new ResponseEntity<>(serviceResult, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ServiceResult<List<PromotionConfiguration>>(false, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(method = POST, headers = ACCEPT_HEADER)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseEntity<ServiceResult<Long>> insert(@RequestBody PromotionConfiguration promotionConfiguration) {
        try {
            ServiceResult<Long> serviceResult = _promotionConfigurationService.insert(promotionConfiguration);
            return new ResponseEntity<>(serviceResult, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ServiceResult<Long>(false, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value="/{promotionConfigurationId}", method = DELETE, headers = ACCEPT_HEADER)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseEntity<ServiceResult<Boolean>> delete(@PathVariable("promotionConfigurationId") long promotionConfigurationId) {
        try {
            ServiceResult<Boolean> serviceResult = _promotionConfigurationService.deletePromotionConfiguration(promotionConfigurationId);
            return new ResponseEntity<>(serviceResult, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ServiceResult<Boolean>(false, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
