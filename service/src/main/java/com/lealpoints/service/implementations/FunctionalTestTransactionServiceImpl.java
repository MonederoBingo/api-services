package com.lealpoints.service.implementations;

import xyz.greatapp.libs.service.context.ThreadContextService;
import com.lealpoints.service.FunctionalTestTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class FunctionalTestTransactionServiceImpl extends BaseServiceImpl implements FunctionalTestTransactionService
{

    @Autowired
    public FunctionalTestTransactionServiceImpl(ThreadContextService threadContextService)
    {
        super(threadContextService);
    }

    @Override
    public void beginTransaction()
    {
        beginForService("30001");
    }

    @Override
    public void rollbackTransaction()
    {
        rollbackForService("30001");
    }

    private void beginForService(String port)
    {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://test.localhost:" + port + "/acceptance_test/transaction/begin";
        restTemplate.getForEntity(url, String.class);
    }

    private void rollbackForService(String port)
    {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://test.localhost:" + port + "/acceptance_test/transaction/rollback";
        restTemplate.getForEntity(url, String.class);
    }
}
