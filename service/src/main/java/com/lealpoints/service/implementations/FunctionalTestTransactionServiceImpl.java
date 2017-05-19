package com.lealpoints.service.implementations;

import com.lealpoints.context.ThreadContextService;
import com.lealpoints.service.FunctionalTestTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class FunctionalTestTransactionServiceImpl extends BaseServiceImpl implements FunctionalTestTransactionService {

    @Autowired
    public FunctionalTestTransactionServiceImpl(ThreadContextService threadContextService) {
        super(threadContextService);
    }

    @Override
    public void beginTransaction() {
        getQueryAgent().beginTransactionForFunctionalTest();

        RestTemplate restTemplate = new RestTemplate();
        String url = "http://test.localhost:20000/acceptance_test/transaction/begin";
        restTemplate.getForEntity(url, String.class);
    }

    @Override
    public void rollbackTransaction() {
        getQueryAgent().rollbackTransactionForFunctionalTest();

        RestTemplate restTemplate = new RestTemplate();
        String url = "http://test.localhost:20000/acceptance_test/transaction/rollback";
        restTemplate.getForEntity(url, String.class);
    }
}
