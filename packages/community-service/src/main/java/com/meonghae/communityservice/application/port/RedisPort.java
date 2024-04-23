package com.meonghae.communityservice.application.port;

import com.meonghae.communityservice.dto.s3.S3ResponseDto;
import java.util.List;

public interface RedisPort {
     String getNickname(String email);

     String getProfileImage(String email);

     List<S3ResponseDto> getReviewImages(Long reviewId);

     String getFcmToken(String email);
}
