package com.example.productservice.repository.gRPC;

import com.google.protobuf.ByteString;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import upload.*;

@Service
@Slf4j
public class UploadGrpcClient {

    @GrpcClient("upload-service")
    private UploadServiceGrpc.UploadServiceBlockingStub uploadServiceStub;

    public String uploadFile(MultipartFile file, String containerName) {
        log.debug("gRPC UploadFile - Request for fileName: {}, containerName: {}",
                file.getOriginalFilename(), containerName);

        try {
            UploadFileRequest request = UploadFileRequest.newBuilder()
                    .setFileName(file.getOriginalFilename() == null ? "" : file.getOriginalFilename())
                    .setContainerName(containerName)
                    .setContent(ByteString.copyFrom(file.getBytes()))
                    .build();

            UploadFileResponse response = uploadServiceStub.uploadFile(request);
            log.info("gRPC UploadFile - Success, url: {}", response.getUrl());
            return response.getUrl();

        } catch (StatusRuntimeException e) {
            log.error("gRPC UploadFile - Failed for fileName: {}, status: {}, description: {}",
                    file.getOriginalFilename(), e.getStatus(), e.getStatus().getDescription(), e);
            throw e;
        } catch (Exception e) {
            log.error("gRPC UploadFile - Failed for fileName: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("Failed to upload file via gRPC", e);
        }
    }

    public void deleteFile(String fileLink) {
        log.debug("gRPC DeleteFile - Request for fileLink: {}", fileLink);

        try {
            DeleteFileRequest request = DeleteFileRequest.newBuilder()
                    .setFileLink(fileLink)
                    .build();

            DeleteFileResponse response = uploadServiceStub.deleteFile(request);
            log.info("gRPC DeleteFile - Success: {}, message: {}", response.getSuccess(), response.getMessage());

        } catch (StatusRuntimeException e) {
            log.error("gRPC DeleteFile - Failed for fileLink: {}, status: {}, description: {}",
                    fileLink, e.getStatus(), e.getStatus().getDescription(), e);
            throw e;
        } catch (Exception e) {
            log.error("gRPC DeleteFile - Failed for fileLink: {}", fileLink, e);
            throw new RuntimeException("Failed to delete file via gRPC", e);
        }
    }
}