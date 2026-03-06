package com.example.uploadfileservice.dto;

import java.io.InputStream;

public record UploadFileCommand(
        InputStream inputStream,
        long size,
        String originalFileName,
        String containerName
) {}