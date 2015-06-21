package com.neerpoints.controller.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/test")
public class TestController extends AbstractApiController {

    @RequestMapping(method = GET)
    public String test() {
        return "it works!";
    }
}
