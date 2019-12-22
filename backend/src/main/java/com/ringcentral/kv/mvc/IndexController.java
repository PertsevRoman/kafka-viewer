package com.ringcentral.kv.mvc;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Profile("spa")
@RequestMapping("/")
public class IndexController extends StaticHandler {
    @GetMapping(value = "/")
    public String index() {
        return "/index.html";
    }
}
