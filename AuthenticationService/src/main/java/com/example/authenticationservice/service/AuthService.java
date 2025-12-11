package com.example.authenticationservice.service;

import com.example.authenticationservice.dto.request.*;
import com.example.authenticationservice.dto.response.IntrospectResponse;
import com.example.authenticationservice.dto.response.LoginResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface AuthService {
    String createUser(UserCreationRequest request, MultipartFile avt);
    LoginResponse login(LoginRequest request);
    String logout(LogoutRequest token);
    LoginResponse refreshToken(String request);
    IntrospectResponse introspect(IntrospectRequest request);
    LoginResponse outboundAuthenticate(String code, String provider);
    String changePassword(ChangePasswordRequest request);
    String blockUser(Integer userId);
    String createPassword(CreatePasswordRequest request);
    String deleteUser(Integer userId);
    Boolean checkUserNameExists(String userName);
}
