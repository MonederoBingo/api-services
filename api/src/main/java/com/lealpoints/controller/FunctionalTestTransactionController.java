package com.lealpoints.controller;

import javax.servlet.http.HttpServletRequest;
import com.lealpoints.service.FunctionalTestTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RequestMapping("/functional_test/transaction")
public class FunctionalTestTransactionController {

    private final FunctionalTestTransactionService _functionalTestTransactionService;

    @Autowired
    public FunctionalTestTransactionController(FunctionalTestTransactionService functionalTestTransactionService) {
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
