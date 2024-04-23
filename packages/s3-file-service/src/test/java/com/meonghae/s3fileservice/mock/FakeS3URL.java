package com.meonghae.s3fileservice.mock;

import java.net.MalformedURLException;
import java.net.URL;

public class FakeS3URL {

    public URL getAmazonS3Url(String bucketName, String fileName) throws MalformedURLException {
        String baseUrl = "https://s3.ap-northeast-2.amazonaws.com/";
        String url = baseUrl + bucketName + "/image/" + fileName;
        return new URL(url);
    }
}
