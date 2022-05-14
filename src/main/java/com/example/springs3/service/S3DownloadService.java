package com.example.springs3.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;

import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM;

@RequiredArgsConstructor
@Service
public class S3DownloadService {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public ResponseEntity<byte[]> download(String fileName) throws IOException {
        S3Object s3Object = amazonS3.getObject(new GetObjectRequest(bucket, fileName));
        S3ObjectInputStream s3ObjectInputStream = s3Object.getObjectContent();
        byte[] bytes = IOUtils.toByteArray(s3ObjectInputStream);
        HttpHeaders httpHeaders = getHttpHeaders(bytes, URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20"));

        return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);
    }

    private HttpHeaders getHttpHeaders(byte[] bytes, String fileName) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(APPLICATION_OCTET_STREAM);
        httpHeaders.setContentLength(bytes.length);
        httpHeaders.setContentDispositionFormData("attachment", fileName);
        return httpHeaders;
    }

}
