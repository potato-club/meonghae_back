package com.meonghae.communityservice.infra.feign.config;

import org.springframework.context.annotation.Bean;

public class FeignHeaderConfig {
    @Bean
    public CustomRequestInterceptor requestInterceptor() {
        return new CustomRequestInterceptor();
    }
}
