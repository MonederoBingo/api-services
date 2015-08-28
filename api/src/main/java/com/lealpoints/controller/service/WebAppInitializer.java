package com.lealpoints.controller.service;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import com.lealpoints.context.PropertyManager;
import org.springframework.web.WebApplicationInitializer;

public class WebAppInitializer implements WebApplicationInitializer {
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        PropertyManager.loadConfiguration();
    }
}
