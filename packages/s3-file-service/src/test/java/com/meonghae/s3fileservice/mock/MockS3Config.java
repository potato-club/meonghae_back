package com.meonghae.s3fileservice.mock;

import com.amazonaws.services.s3.AmazonS3Client;
import com.meonghae.s3fileservice.core.config.S3Config;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class MockS3Config extends S3Config {

    @Bean
    @Primary
    @Override
    public AmazonS3Client amazonS3Client() {
        return Mockito.mock(AmazonS3Client.class);
    }
}
