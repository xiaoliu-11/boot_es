package com.example.boot_es.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HtmlController {
    @GetMapping("/test")
    public String test(){
        return "test";

    }
}
