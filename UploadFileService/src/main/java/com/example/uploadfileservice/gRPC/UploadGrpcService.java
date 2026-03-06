package com.example.uploadfileservice.gRPC;

import com.example.uploadfileservice.dto.UploadFileCommand;
import net.devh.boot.grpc.server.service.GrpcService;
import upload.DeleteFileRequest;
import upload.DeleteFileResponse;
import upload.UploadFileRequest;
import upload.UploadFileResponse;
import upload.UploadServiceGrpc;
import com.example.uploadfileservice.service.UploadService;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;

@GrpcService
public class UploadGrpcService extends UploadServiceGrpc.UploadServiceImplBase {

	@Autowired
	private UploadService uploadService;

    @Override
    public void uploadFile(
            UploadFileRequest request,
            StreamObserver<UploadFileResponse> responseObserver) {

        try {
            UploadFileCommand command = new UploadFileCommand(
                    new ByteArrayInputStream(
                            request.getContent().toByteArray()
                    ),
                    request.getContent().size(),
                    request.getFileName(),
                    request.getContainerName()
            );

            String url = uploadService.uploadFile(command);

            responseObserver.onNext(
                    UploadFileResponse.newBuilder()
                            .setUrl(url)
                            .build()
            );
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
