package com.example.profileservice.repository.httpClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
@FeignClient(
        name = "upload-file-service",
        url = "http://localhost:8000/api/v1/internal/azure"
)
public interface UploadClient {

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    String uploadFile(
            @RequestPart("file") MultipartFile file,
            @RequestPart("containerName") String containerName
    );

    @DeleteMapping("/delete")
    void deleteFile(@RequestParam String fileLink);
}