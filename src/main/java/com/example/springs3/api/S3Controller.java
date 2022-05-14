package com.example.springs3.api;

import com.example.springs3.service.S3DownloadService;
import com.example.springs3.service.S3DownloadZipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
@Controller
public class S3Controller {

    private final S3DownloadService s3DownloadService;
    private final S3DownloadZipService s3DownloadZipService;

    @GetMapping("/download/{fileName}")
    public ResponseEntity<byte[]> download(@PathVariable String fileName) throws IOException {
        return s3DownloadService.download(fileName);
    }

    @GetMapping("/download-zip")
    public ResponseEntity<StreamingResponseBody> downloadZip(HttpServletResponse response) {
        return s3DownloadZipService.downloadZip(response);
    }
}
