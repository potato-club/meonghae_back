package com.meonghae.communityservice.Config;

import org.springframework.context.annotation.Bean;

public class FeignHeaderConfig {
    @Bean
    public CustomRequestInterceptor requestInterceptor() {
        return new CustomRequestInterceptor();
    }
}
