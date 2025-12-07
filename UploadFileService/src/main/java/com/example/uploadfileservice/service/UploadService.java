package com.example.uploadfileservice.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public interface UploadService {
    String uploadFile(MultipartFile file, String containerName) throws IOException;
    void deleteFile(String fileLink);
}
