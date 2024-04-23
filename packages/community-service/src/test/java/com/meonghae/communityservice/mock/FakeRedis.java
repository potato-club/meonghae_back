package com.meonghae.communityservice.mock;

import com.meonghae.communityservice.application.port.RedisPort;
import com.meonghae.communityservice.dto.s3.S3ResponseDto;

import java.util.List;

public class FakeRedis implements RedisPort {
    @Override
    public String getNickname(String email) {
        return null;
    }

    @Override
    public String getProfileImage(String email) {
        return null;
    }

    @Override
    public List<S3ResponseDto> getReviewImages(Long reviewId) {
        return null;
    }

    @Override
    public String getFcmToken(String email) {
        return null;
    }
}
