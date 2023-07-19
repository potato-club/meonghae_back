package com.moenghae.apigatewayservice.config;

import com.moenghae.apigatewayservice.error.CustomExceptionHandler;
import org.springframework.boot.web.client.RestTemplateBuilder;
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

//    @Bean
//    public CustomExceptionHandler customExceptionHandler() {
//        return new CustomExceptionHandler();
//    }
}
