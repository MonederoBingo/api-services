package com.monederobingo.controller;

import com.monederobingo.controller.base.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/test")
public class TestApiController extends BaseController {

    @RequestMapping(method = GET)
    public String test() {
        throw new RuntimeException("chale");
    }
}
