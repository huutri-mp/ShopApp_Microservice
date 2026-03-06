package com.example.uploadfileservice.service;

import com.example.uploadfileservice.dto.UploadFileCommand;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public interface UploadService {
    String uploadFile(UploadFileCommand command) throws IOException;
    void deleteFile(String fileLink);
}
