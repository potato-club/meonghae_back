package com.meonghae.profileservice.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.gson.JsonParseException;
import com.meonghae.profileservice.dto.schedule.AlarmDto;
import com.meonghae.profileservice.dto.fcm.FcmMessage;
import com.meonghae.profileservice.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class FcmConfig {
    private final ObjectMapper objectMapper;
    private final RedisService redisService;
    private final String ApiUrl = "https://fcm.googleapis.com/v1/projects/meonghae-b9c8b/messages:send";
//    @Value("${firebase:}")
//    private String firebaseJson;

    public void sendMessageTo(AlarmDto alarmDto) throws IOException {
        String message = makeMessage(alarmDto);

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(message,
                MediaType.get("application/json; charset=utf-8"));
        log.info("http 헤더 만들기");
        Request request = new Request.Builder()
                .url(ApiUrl)
                .post(requestBody)
                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                .build();
        log.info("보내기 전");
        log.info(requestBody.toString());
        Response response = client.newCall(request).execute();
        log.info("보낸 후");
        System.out.println(response.body().string());
    }

    private String makeMessage(AlarmDto alarmDto) throws JsonParseException, JsonProcessingException {
        
        FcmMessage message = FcmMessage.builder()
                .to(redisService.getFcmToken(alarmDto.getUserEmail()))
                .notification(FcmMessage.Notification.builder()
                        .title("멍해")
                        .body(alarmDto.getText())
                        .build())
                .build();
        log.info("메시지 만듦");
        return objectMapper.writeValueAsString(message);
    }

    // firebase로 부터 access token을 가져온다. -> 이 토큰은 사용자 고유식별 토큰 아님
    private String getAccessToken() throws IOException {
        log.info("엑세스 토큰 받아오기");
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new FileInputStream("/app/config"))
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

        googleCredentials.refreshIfExpired();
        log.info("엑세스 토큰 받아옴");
        return googleCredentials.getAccessToken().getTokenValue();
    }

}
