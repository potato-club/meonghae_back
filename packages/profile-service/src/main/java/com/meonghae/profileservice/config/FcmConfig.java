package com.meonghae.profileservice.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.gson.JsonParseException;
import com.meonghae.profileservice.dto.calendar.AlarmDto;
import com.meonghae.profileservice.dto.fcm.FcmMessage;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FcmConfig {
    @Qualifier("jasyptStringEncryptor")
    private final StringEncryptor stringEncryptor;
    private final ObjectMapper objectMapper;
    private final String ApiUrl = "https://fcm.googleapis.com/v1/projects/meonghae-b9c8b/messages:send";

    public void sendMessageTo(AlarmDto alarmDto) throws IOException {
        String message = makeMessage(alarmDto);

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(message,
                MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(ApiUrl)
                .post(requestBody)
                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                .build();

        Response response = client.newCall(request).execute();

        System.out.println(response.body().string());
    }

    private String makeMessage(AlarmDto alarmDto) throws JsonParseException, JsonProcessingException {

        FcmMessage message = FcmMessage.builder()
                .to(alarmDto.getToken())
                .notification(FcmMessage.Notification.builder()
                        .title("멍해")
                        .body(alarmDto.getText())
                        .build())
                .build();

        return objectMapper.writeValueAsString(message);
    }

    // firebase로 부터 access token을 가져온다. -> 이 토큰은 사용자 고유식별 토큰 아님
    private String getAccessToken() throws IOException {
        String firebaseConfigPath = "firebase/meonghae-b9c8b-firebase-adminsdk-21gn2-745e8254ce.json";
        String encryptedKey = loadEncryptedKey(firebaseConfigPath);
        String decryptedKey = decryptKey(encryptedKey);

        InputStream keyStream = new ByteArrayInputStream(decryptedKey.getBytes(StandardCharsets.UTF_8));

        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(keyStream)
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

        googleCredentials.refreshIfExpired();
        return googleCredentials.getAccessToken().getTokenValue();
    }
    private String loadEncryptedKey(String path) throws IOException {
        Path encryptedKeyPath = new ClassPathResource(path).getFile().toPath();
        return Files.readString(encryptedKeyPath, StandardCharsets.UTF_8);
    }

    private String decryptKey(String encryptedKey) {
        return stringEncryptor.decrypt(encryptedKey);
    }
}
