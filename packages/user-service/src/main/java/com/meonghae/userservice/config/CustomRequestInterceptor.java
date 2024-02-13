package com.meonghae.userservice.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class CustomRequestInterceptor implements RequestInterceptor {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String REFRESH_HEADER = "RefreshToken";

    @Override
    public void apply(RequestTemplate template) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(attributes != null) {
            String authorizationHeader = attributes.getRequest().getHeader(AUTHORIZATION_HEADER);
            String refreshHeader = attributes.getRequest().getHeader(REFRESH_HEADER);
            if (authorizationHeader != null) {
                // 가져온 Header 값을 Feign 요청의 Header 에 추가함
                template.header(AUTHORIZATION_HEADER, authorizationHeader);
                template.header(REFRESH_HEADER, refreshHeader);
            }
        }
    }
}
