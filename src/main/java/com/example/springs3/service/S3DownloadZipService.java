package com.example.springs3.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@RequiredArgsConstructor
@Service
public class S3DownloadZipService {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public ResponseEntity<StreamingResponseBody> downloadZip(HttpServletResponse response) {
        List<String> fileNames = getFileNames();

        StreamingResponseBody streamResponseBody = out -> {
            ServletOutputStream servletOutputStream = response.getOutputStream();
            ZipOutputStream zipOutputStream = new ZipOutputStream(servletOutputStream);

            fileNames.forEach(fileName -> {
                ZipEntry zipEntry = new ZipEntry(fileName);
                try {
                    zipOutputStream.putNextEntry(zipEntry);
                    StreamUtils.copy(getS3InputStream(fileName), zipOutputStream);
                    zipOutputStream.closeEntry();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            zipOutputStream.finish();
            zipOutputStream.close();
            servletOutputStream.close();
        };

        final String zipFileName = "images.zip";
        response.setStatus(HttpServletResponse.SC_OK);
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + zipFileName);

        return ResponseEntity.ok(streamResponseBody);
    }

    private S3ObjectInputStream getS3InputStream(String fileName) throws IOException {
        log.info("fileName={}", fileName);
        S3Object s3Object = amazonS3.getObject(new GetObjectRequest(bucket, fileName));
        return s3Object.getObjectContent();
    }

    private List<String> getFileNames() {
        List<String> fileNames = new ArrayList<>();
        fileNames.add("profile.jpeg");
        fileNames.add("docker.png");
        return fileNames;
    }

}
