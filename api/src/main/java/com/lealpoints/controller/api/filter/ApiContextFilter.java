package com.lealpoints.controller.api.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import com.lealpoints.context.Environment;
import com.lealpoints.context.ThreadContextService;
import com.lealpoints.service.implementations.AuthenticationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ApiContextFilter implements Filter {

    @Autowired
    private ThreadContextService _threadContextService;
    @Autowired
    private AuthenticationServiceImpl _authenticationService;

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
        if (request.getMethod().equals("OPTIONS")) {
            return true;
        }
        final String userId = request.getHeader("User-Id");
        final String apiKey = request.getHeader("Api-Key");
        return userId != null && apiKey != null && _authenticationService.isValidApiKey(userId, apiKey).isSuccess();
    }

    public void init(FilterConfig filterConfig) {
    }

    public void destroy() {
    }

    private void initializeContext(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        _threadContextService.initializeContext(getEnvironment(httpServletRequest), httpServletRequest.getHeader("language"));
        setHeaders(httpServletResponse, httpServletRequest);
    }

    private void setHeaders(HttpServletResponse response, HttpServletRequest request) {
        List<String> incomingURLs =
            Arrays.asList("http://localhost:8080", "http://test.localhost:8080", "http://www.lealpoints.com", "http://test.lealpoints.com");
        String clientOrigin = request.getHeader("origin") != null ? request.getHeader("origin") : request.getHeader("referrer");
        int myIndex = incomingURLs.indexOf(clientOrigin);
        if (myIndex != -1) {
            response.addHeader("Access-Control-Allow-Headers", "Content-Type,x-requested-with,Authorization,Api-Key,User-Id,language");
            response.addHeader("Access-Control-Max-Age", "360");
            response.addHeader("Access-Control-Allow-Methods", "GET,POST,DELETE,PUT");
            response.addHeader("Access-Control-Allow-Origin", clientOrigin);
            response.addHeader("Access-Control-Allow-Credentials", "true");
            response.addHeader("Access-Control-Allow-Credentials", "true");
        }
    }

    private Environment getEnvironment(HttpServletRequest request) {
        switch (request.getServerName()) {
            case "services.lealpoints.com":
                return Environment.PROD;
            case "test.services.lealpoints.com":
                return Environment.UAT;
            case "test.localhost":
                return Environment.FUNCTIONAL_TEST;
            default:
                return Environment.DEV;
        }
    }
}