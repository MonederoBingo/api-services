package com.lealpoints.controller;

import com.lealpoints.service.FunctionalTestTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RequestMapping("/functional_test/transaction")
public class AutomatedTestTransactionController {

    private final FunctionalTestTransactionService _functionalTestTransactionService;

    @Autowired
    public AutomatedTestTransactionController(FunctionalTestTransactionService functionalTestTransactionService) {
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
