package com.lealpoints.service.implementations;

import com.lealpoints.context.ThreadContextService;
import com.lealpoints.service.FunctionalTestTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FunctionalTestTransactionServiceImpl extends BaseServiceImpl implements FunctionalTestTransactionService {

    @Autowired
    public FunctionalTestTransactionServiceImpl(ThreadContextService threadContextService) {
        super(threadContextService);
    }

    @Override
    public void beginTransaction() {
        getQueryAgent().beginTransactionForFunctionalTest();
    }

    @Override
    public void rollbackTransaction() {
        getQueryAgent().rollbackTransactionForFunctionalTest();
    }
}
