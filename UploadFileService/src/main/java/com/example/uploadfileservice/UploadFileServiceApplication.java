package com.example.uploadfileservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class UploadFileServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UploadFileServiceApplication.class, args);
    }

}
