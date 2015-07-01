package com.neerpoints.controller.api.filter;

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
import com.neerpoints.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ApiContextFilter implements Filter {

    @Autowired
    private ThreadContextService _threadContextService;
    @Autowired
    private AuthenticationService _authenticationService;

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        final HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        initializeContext(httpServletRequest, httpServletResponse);
        if (!isValidCall(httpServletRequest)) {
            httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        chain.doFilter(request, response);
    }

    private boolean isValidCall(HttpServletRequest request) {
//        if (request.getMethod().equals("OPTIONS")) {
//            return true;
//        }
//        final String userId = request.getHeader("User-Id");
//        final String apiKey = request.getHeader("Api-Key");
//        return userId != null && apiKey != null && _authenticationService.isValidApiKey(userId, apiKey).isSuccess();
        return true;
    }

    public void init(FilterConfig filterConfig) {
    }

    public void destroy() {
    }

    private void initializeContext(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        final boolean isProdEnvironment = isProdEnvironment(httpServletRequest);
        _threadContextService.initializeContext(isProdEnvironment, httpServletRequest.getHeader("language"));
        setHeaders(httpServletResponse, isProdEnvironment);
    }

    private void setHeaders(HttpServletResponse response, boolean isProdEnvironment) {
        response.addHeader("Access-Control-Allow-Headers", "Content-Type,x-requested-with,Authorization, Api-Key, User-Id, language");
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