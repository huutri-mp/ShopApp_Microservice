package com.example.uploadfileservice.gRPC;

import net.devh.boot.grpc.server.service.GrpcService;
import upload.DeleteFileRequest;
import upload.DeleteFileResponse;
import upload.UploadFileRequest;
import upload.UploadFileResponse;
import upload.UploadServiceGrpc;
import com.example.uploadfileservice.service.UploadService;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;

@GrpcService
public class UploadGrpcService extends UploadServiceGrpc.UploadServiceImplBase {

	@Autowired
	private BlobServiceClient blobServiceClient;

	@Autowired
	private UploadService uploadService;

	@Override
	public void uploadFile(UploadFileRequest request, StreamObserver<UploadFileResponse> responseObserver) {
		try {
			String containerName = request.getContainerName();
			String fileName = request.getFileName();

			if (containerName == null || containerName.isEmpty()) {
				responseObserver.onNext(UploadFileResponse.newBuilder().setUrl("").build());
				responseObserver.onCompleted();
				return;
			}

			BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
			BlobClient blobClient = containerClient.getBlobClient(fileName);

			byte[] data = request.getContent().toByteArray();
			try (ByteArrayInputStream in = new ByteArrayInputStream(data)) {
				blobClient.upload(in, data.length, true);
			}

			String url = blobClient.getBlobUrl();
			responseObserver.onNext(UploadFileResponse.newBuilder().setUrl(url).build());
			responseObserver.onCompleted();
		} catch (Exception e) {
			responseObserver.onError(e);
		}
	}

	@Override
	public void deleteFile(DeleteFileRequest request, StreamObserver<DeleteFileResponse> responseObserver) {
		try {
			String fileLink = request.getFileLink();
			uploadService.deleteFile(fileLink);
			responseObserver.onNext(DeleteFileResponse.newBuilder().setSuccess(true).setMessage("deleted").build());
			responseObserver.onCompleted();
		} catch (Exception e) {
			responseObserver.onNext(DeleteFileResponse.newBuilder().setSuccess(false).setMessage(e.getMessage()).build());
			responseObserver.onCompleted();
		}
	}
}
