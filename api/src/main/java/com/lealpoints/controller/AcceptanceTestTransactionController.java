package com.lealpoints.controller;

import com.lealpoints.service.FunctionalTestTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/acceptance_test/transaction")
public class AcceptanceTestTransactionController {

    private final FunctionalTestTransactionService _functionalTestTransactionService;

    @Autowired
    public AcceptanceTestTransactionController(FunctionalTestTransactionService functionalTestTransactionService) {
        _functionalTestTransactionService = functionalTestTransactionService;
    }

    @RequestMapping(value = "/begin", method = GET)
    public void begin(HttpServletRequest request) throws Exception {
        if (request.getServerName().equals("test.localhost")) {
            _functionalTestTransactionService.beginTransaction();
        }
    }

    @RequestMapping(value = "/rollback", method = GET)
    public void rollback(HttpServletRequest request) throws Exception {
        if (request.getServerName().equals("test.localhost")) {
            _functionalTestTransactionService.rollbackTransaction();
        }
    }
}
