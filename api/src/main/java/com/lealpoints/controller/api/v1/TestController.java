package com.lealpoints.controller.api.v1;

import com.lealpoints.controller.base.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/api/v1/test")
public class TestController extends BaseController {

    @RequestMapping(method = GET)
    public String test() {
        return "it works! :)";
    }
}
