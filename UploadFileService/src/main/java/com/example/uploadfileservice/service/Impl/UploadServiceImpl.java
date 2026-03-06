package com.example.uploadfileservice.service.Impl;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.example.uploadfileservice.dto.UploadFileCommand;
import com.example.uploadfileservice.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class UploadServiceImpl implements UploadService {

   @Autowired
   private BlobServiceClient blobServiceClient;

    @Override
    public String uploadFile(UploadFileCommand command) {

        String fileName =
                UUID.randomUUID() + "-" + command.originalFileName();

        BlobContainerClient containerClient =
                blobServiceClient.getBlobContainerClient(command.containerName());

        BlobClient blobClient =
                containerClient.getBlobClient(fileName);

        blobClient.upload(
                command.inputStream(),
                command.size(),
                true
        );

        return blobClient.getBlobUrl();
    }

    public void deleteFile(String fileLink) {

        if(fileLink.isEmpty()){
            return;
        }

        String[] parts = fileLink.split("/");
        String fileName = parts[parts.length - 1];
        String containerName = parts[parts.length - 2];
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
        BlobClient blobClient = containerClient.getBlobClient(fileName);
        blobClient.delete();
    }
}
