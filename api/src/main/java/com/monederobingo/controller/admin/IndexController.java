package com.monederobingo.controller.admin;

import com.monederobingo.controller.base.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/")
public class IndexController extends BaseController {

    @RequestMapping(method = RequestMethod.GET)
    public String main() {
        return "index";
    }
}
