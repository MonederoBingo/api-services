package com.lealpoints.controller.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import com.lealpoints.common.PropertyManager;
import org.springframework.web.WebApplicationInitializer;

public class WebAppInitializer implements WebApplicationInitializer {
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        PropertyManager.loadConfiguration();
    }
}
