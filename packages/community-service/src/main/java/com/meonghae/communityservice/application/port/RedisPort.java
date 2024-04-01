package com.meonghae.communityservice.application.port;

import com.meonghae.communityservice.dto.s3.S3Response;
import java.util.List;

public interface RedisPort {
     String getNickname(String email);

     String getProfileImage(String email);

     List<S3Response> getReviewImages(Long reviewId);

     String getFcmToken(String email);
}
