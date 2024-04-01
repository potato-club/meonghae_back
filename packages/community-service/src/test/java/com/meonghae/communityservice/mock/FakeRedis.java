package com.meonghae.communityservice.mock;

import com.meonghae.communityservice.application.port.RedisPort;
import com.meonghae.communityservice.dto.s3.S3Response;

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
    public List<S3Response> getReviewImages(Long reviewId) {
        return null;
    }

    @Override
    public String getFcmToken(String email) {
        return null;
    }
}
