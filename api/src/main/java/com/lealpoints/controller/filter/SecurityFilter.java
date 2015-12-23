package com.lealpoints.controller.filter;

import com.lealpoints.service.implementations.AuthenticationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class SecurityFilter implements Filter {

    @Autowired
    private AuthenticationServiceImpl _authenticationService;

    public void init(FilterConfig filterConfig) {
    }

    public void destroy() {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        final HttpServletResponse httpServletResponse = (HttpServletResponse) response;
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
        return userId != null && apiKey != null &&
                _authenticationService.isValidApiKey(Integer.parseInt(userId), apiKey).isSuccess();
    }
}