package com.monederobingo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.greatapp.libs.service.context.ContextFilter;
import xyz.greatapp.libs.service.context.ThreadContextService;
import xyz.greatapp.libs.service.context.ThreadContextServiceImpl;
import xyz.greatapp.libs.service.location.ServiceLocator;

@Configuration
public class AppConfiguration {

    @Bean
    public ThreadContextService getThreadContextService() {
        return new ThreadContextServiceImpl();
    }

    @Bean
    public ServiceLocator getServiceLocator() {
        return new ServiceLocator();
    }

    @Bean
    public ContextFilter getContextFilter() {
        return new ContextFilter(getThreadContextService());
    }
}
