package com.neerpoints.api.controller;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import com.neerpoints.service.PromotionService;
import com.neerpoints.service.model.PromotionApplying;
import com.neerpoints.service.model.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/promotions")
public class PromotionController extends AbstractRestController {

    private final PromotionService _promotionService;

    @Autowired
    public PromotionController(PromotionService promotionService) {
        _promotionService = promotionService;
    }

    @RequestMapping(method = POST, headers = ACCEPT_HEADER)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseEntity<ServiceResult<Long>> applyPromotion(@RequestBody PromotionApplying promotionApplying) {
        try {
            ServiceResult<Long> serviceResult = _promotionService.applyPromotion(promotionApplying);
            return new ResponseEntity<>(serviceResult, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ServiceResult<Long>(false, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
