package com.monederobingo.filters;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static java.lang.Integer.MIN_VALUE;
import static java.util.Arrays.asList;
import static java.util.Objects.nonNull;

/**
 * It returns CORS headers for valid clients.
 */
@Component
@Order(MIN_VALUE + 1)
public class SecurityFilter extends GenericFilterBean
{
    private final List<String> WHITE_LIST_CLIENTS = asList(
            "http://localhost:8080",
            "http://localhost",
            "https://localhost:8080",
            "https://localhost",
            "http://test.localhost",
            "https://test.localhost",
            "http://www.monederobingo.com",
            "https://www.monederobingo.com",
            "http://uat.monederobingo.com",
            "https://uat.monederobingo.com");

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        final HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        final HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        System.out.println(httpServletRequest.getHeader("origin"));
        if (isValidClientUrl(httpServletRequest.getHeader("origin")))
        {
            setCORSHeaders(httpServletResponse, httpServletRequest);
        }
        if (!httpServletRequest.getMethod().equals("OPTIONS"))
        {
            chain.doFilter(request, response);
        }
    }

    private void setCORSHeaders(HttpServletResponse response, HttpServletRequest request)
    {
        response.setHeader("Access-Control-Allow-Headers", "Content-Type,x-requested-with,Authorization,x-xsrf-token,Api-Key,User-Id");
        response.setHeader("Access-Control-Max-Age", "360");
        response.setHeader("Access-Control-Allow-Methods", "GET,POST,DELETE,PUT");
        response.setHeader("Access-Control-Allow-Origin", getClientUrl(request));
        response.setHeader("Access-Control-Allow-Credentials", "true");
    }

    private boolean isValidClientUrl(String origin)
    {
        return WHITE_LIST_CLIENTS.contains(origin);
    }

    private String getClientUrl(HttpServletRequest request)
    {
        if (nonNull(request.getHeader("origin")))
        {
            return request.getHeader("origin");
        }
        else if (nonNull(request.getHeader("referer")))
        {
            return request.getHeader("referer");
        }
        return "";
    }
}
