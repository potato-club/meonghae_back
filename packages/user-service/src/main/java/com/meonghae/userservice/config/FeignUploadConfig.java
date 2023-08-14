package com.meonghae.userservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.openfeign.support.AbstractFormWriter;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

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
