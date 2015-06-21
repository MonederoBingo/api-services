package com.neerpoints.controller.auth.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import com.neerpoints.context.ThreadContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthContextFilter implements Filter {

    @Autowired
    private ThreadContextService _threadContextService;

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        initializeContext(request, response);
        chain.doFilter(request, response);
    }

    public void init(FilterConfig filterConfig) {
    }

    public void destroy() {
    }

    private void initializeContext(ServletRequest req, ServletResponse response) {
        final boolean isProdEnvironment = isProdEnvironment(req);
        _threadContextService.initializeContext(isProdEnvironment, ((HttpServletRequest) req).getHeader("language"));
        setHeaders((HttpServletResponse) response, isProdEnvironment);
    }

    private void setHeaders(HttpServletResponse response, boolean isProdEnvironment) {
        response.addHeader("Access-Control-Allow-Headers", "Content-Type,x-requested-with,Authorization, language");
        response.addHeader("Access-Control-Max-Age", "360");
        response.addHeader("Access-Control-Allow-Methods", "GET,POST,DELETE,PUT");
        response.addHeader("Access-Control-Allow-Origin", isProdEnvironment ? "http://www.neerpoints.com" : "http://localhost:8080");
        response.addHeader("Access-Control-Allow-Credentials", "true");
        response.addHeader("Access-Control-Allow-Credentials", "true");
    }

    private boolean isProdEnvironment(ServletRequest req) {
        String serverName = req.getServerName();
        return serverName.equals("services-neerpoints.rhcloud.com");
    }

//    private boolean isTestEnvironment(ServletRequest req) {
//        String serverName = req.getServerName();
//        String[] serverNameParts = serverName.split("\\.");
//        return serverNameParts.length > 0 && serverNameParts[0].equals("test");
//    }
}