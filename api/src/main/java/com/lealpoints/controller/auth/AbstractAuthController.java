package com.lealpoints.controller.auth;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Defines a generic behaviour for a RestController
 */
public class AbstractAuthController {
    public static final String ACCEPT_HEADER = "Accept=" + MediaType.APPLICATION_JSON;

    @RequestMapping(value = {"", "/*", "/*/*"}, method = RequestMethod.OPTIONS)
    public void commonOptions() throws IOException {
    }
}