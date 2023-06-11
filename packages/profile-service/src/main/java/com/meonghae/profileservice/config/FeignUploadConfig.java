package com.meonghae.profileservice.config;

import feign.form.spring.SpringFormEncoder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

public class FeignUploadConfig {
    @Bean
    public SpringFormEncoder multipartFormEncoder() {
        ObjectFactory<HttpMessageConverters> convertersFactory = new HttpMessageConvertersFactory();
        return new SpringFormEncoder(new SpringEncoder(convertersFactory));
    }

    public class HttpMessageConvertersFactory implements ObjectFactory<HttpMessageConverters> {
        @Override
        public HttpMessageConverters getObject() throws BeansException {
            return new HttpMessageConverters(new RestTemplate().getMessageConverters());
        }
    }
}
