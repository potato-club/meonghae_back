package com.moenghae.apigatewayservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class controller {
    @GetMapping("/health-check")
    public String check() {
        return "health-checking";
    }
}
