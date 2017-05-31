package com.monederobingo.controller.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

@Configuration
@ComponentScan({ "com.monederobingo", "com.lealpoints" })
public class SpringConfig extends ResourceServerConfigurerAdapter
{
    @Override
    public void configure(HttpSecurity http) throws Exception
    {
        http.csrf()
                .disable()
                .authorizeRequests()
                .antMatchers("/")
                .permitAll()
                .antMatchers("/api/**")
                .authenticated();
    }
}
