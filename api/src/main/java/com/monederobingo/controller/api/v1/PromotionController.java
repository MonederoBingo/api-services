package com.monederobingo.controller.api.v1;

import com.monederobingo.controller.base.BaseController;
import com.lealpoints.service.PromotionService;
import com.lealpoints.service.model.PromotionApplying;
import com.lealpoints.service.response.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/api/v1/promotions")
public class PromotionController extends BaseController
{

    private final PromotionService _promotionService;

    @Autowired
    public PromotionController(PromotionService promotionService) {
        _promotionService = promotionService;
    }

    @RequestMapping(method = POST, headers = ACCEPT_HEADER)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseEntity<ServiceResult<Long>> applyPromotion(@RequestBody PromotionApplying promotionApplying) {
        ServiceResult<Long> serviceResult = _promotionService.applyPromotion(promotionApplying);
        return new ResponseEntity<>(serviceResult, HttpStatus.OK);
    }
}
