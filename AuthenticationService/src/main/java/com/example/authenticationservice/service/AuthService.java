package com.example.authenticationservice.service;

import com.example.authenticationservice.dto.request.*;
import com.example.authenticationservice.dto.response.LoginResponse;
import com.example.authenticationservice.entity.AuthenUser;
import com.example.commonlib.dto.PagingResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface AuthService {
    String createUser(UserCreationRequest request, MultipartFile avt);
    LoginResponse login(LoginRequest request);
    String logout(LogoutRequest token);
    LoginResponse refreshToken(String request);
    Boolean introspect(String token);
    LoginResponse outboundAuthenticate(String code, String provider);
    String changePassword(ChangePasswordRequest request);
    String updateAuthUser(Integer userId, UpdateAuthUser updateAuthUser);
    String createPassword(CreatePasswordRequest request);
    String deleteUser(Integer userId);
    Boolean checkUserNameExists(String userName);
    PagingResponse<AuthenUser> getUsers(int page, int size, Boolean enabled, List<Integer> userIds, String role, String sort);
}
