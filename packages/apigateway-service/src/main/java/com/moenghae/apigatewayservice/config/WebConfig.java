package com.moenghae.apigatewayservice.config;

import com.moenghae.apigatewayservice.error.MyWebExceptionHandler;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class WebConfig {

    @Bean
    public RestTemplateBuilder restTemplateBuilder() {
        return new RestTemplateBuilder();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ErrorWebExceptionHandler myExceptionHandler() {
        return new MyWebExceptionHandler();
    }
}
