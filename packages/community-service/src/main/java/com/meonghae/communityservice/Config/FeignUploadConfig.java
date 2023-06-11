package com.meonghae.communityservice.Config;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.form.spring.SpringFormEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.AbstractFormWriter;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class FeignUploadConfig extends AbstractFormWriter {
    private final ObjectMapper objectMapper;

    @Override
    protected MediaType getContentType() {
        return MediaType.APPLICATION_JSON;
    }

    @Override
    protected String writeAsString(Object object) throws IOException {
        return objectMapper.writeValueAsString(object);
    }
}
