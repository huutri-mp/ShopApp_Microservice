package com.example.uploadfileservice.controller;

import com.example.uploadfileservice.constan.UrlConstant;
import com.example.uploadfileservice.service.UploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@Slf4j
@RestController
@RequestMapping(UrlConstant.API_V1_UPLOAD_INTERNAL)
public class InternalUploadController {

    @Autowired
    private UploadService uploadService;

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam(value = "file") MultipartFile file,
                                    @RequestParam("containerName") String containerName){
        log.info("Upload file request received");
        try {
            String url = uploadService.uploadFile(file, containerName);
            return ResponseEntity.ok(url);
        } catch (Exception e){
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
    @DeleteMapping("/delete")
    public ResponseEntity<Void> delete(@RequestParam String fileLink) {
        log.info("Delete file request received");
        try {
            uploadService.deleteFile(fileLink);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}