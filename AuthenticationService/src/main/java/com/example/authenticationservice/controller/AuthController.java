package com.example.authenticationservice.controller;

import com.example.authenticationservice.constant.UrlConstan;
import com.example.authenticationservice.dto.request.*;
import com.example.authenticationservice.dto.response.LoginResponse;
import com.example.authenticationservice.service.AuthService;
import com.example.commonlib.dto.ApiResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.multipart.MultipartFile;


@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(UrlConstan.API_V1_AUTH)
public class AuthController {
    @Autowired
    private AuthService authService;

    @Value("${jwt.refresh-valid-duration}")
    private long REFRESH_VALID_DURATION;

    @PostMapping("/register")
    public ApiResponse<String> register(
            @RequestPart ("userData")  UserCreationRequest request,
            @RequestPart (value = "avt", required = false) MultipartFile avt ) {
        log.info("Register request: {}", request.getUserName(), request.getEmail(), request.getFullName());
        String response = authService.createUser(request, avt);
        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setMessage(response);
        return apiResponse;
    }
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@RequestBody LoginRequest loginRequest,
                                                     HttpServletRequest httpRequest,
                                                     HttpServletResponse httpResponse) {

        LoginResponse loginResponse = authService.login(loginRequest);
        String token = loginResponse.getToken();
        String refreshToken = loginResponse.getRefreshToken();

        ResponseCookie cookie = ResponseCookie.from("refresh_token", refreshToken )
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(REFRESH_VALID_DURATION /1000) // convert ms -> s
            .sameSite("none")
            .build();

        CookieCsrfTokenRepository csrfRepo = CookieCsrfTokenRepository.withHttpOnlyFalse();
        CsrfToken csrfToken = csrfRepo.generateToken(httpRequest);
        csrfRepo.saveToken(csrfToken, httpRequest, httpResponse);

        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("Đăng nhập thành công");
        apiResponse.setData(token);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(apiResponse);
    }
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(
            HttpServletResponse response,
            @CookieValue(name = "refresh_token") String refreshTokenCookie) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String token = ((JwtAuthenticationToken) auth).getToken().getTokenValue();

        LogoutRequest logoutRequest = LogoutRequest.builder()
                .accessToken(token)
                .refreshToken(refreshTokenCookie)
                .build();
        String msg = authService.logout(logoutRequest);

        Cookie refreshCookie = new Cookie("refresh_token", null);
        refreshCookie.setPath("/");
        refreshCookie.setHttpOnly(true);
        refreshCookie.setMaxAge(0);
        response.addCookie(refreshCookie);

        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setMessage(msg);
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/outbound/authentication")
    public ResponseEntity<ApiResponse<String>> outboundAuthenticate(@RequestParam("code") String code,
                                                                    @RequestParam("provider") String provider,
                                                                    HttpServletRequest httpRequest,
                                                                    HttpServletResponse httpResponse  ) {

        LoginResponse loginResponse = authService.outboundAuthenticate(code, provider);
        String token = loginResponse.getToken();
        String refreshToken = loginResponse.getRefreshToken();

        ResponseCookie cookie = ResponseCookie.from("refresh_token", refreshToken )
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(REFRESH_VALID_DURATION /1000) // convert ms -> s
                .sameSite("none")
                .build();

        CookieCsrfTokenRepository csrfRepo = CookieCsrfTokenRepository.withHttpOnlyFalse();
        CsrfToken csrfToken = csrfRepo.generateToken(httpRequest);
        csrfRepo.saveToken(csrfToken, httpRequest, httpResponse);

        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("Đăng nhập thành công");
        apiResponse.setData(token);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(apiResponse);
    }

    @PutMapping("/change-password")
    public ApiResponse<String> changePassword(@RequestBody ChangePasswordRequest request) {
        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setData(authService.changePassword(request));
        return apiResponse;
    }

    @PreAuthorize("authentication.principal.claims['role'] == 'ADMIN'")
    @GetMapping("/block-user/{id}")
    public ApiResponse<String> blockUser(@PathVariable("id") Integer userId) {
        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setData(authService.blockUser(userId));
        return apiResponse;
    }

    @PostMapping("/create-password")
    public ApiResponse<String> createPassword(@RequestBody CreatePasswordRequest request) {
        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setData(authService.createPassword(request));
        return apiResponse;
    }

    @PostMapping("/refresh-token")
    public ResponseEntity refreshToken(@CookieValue(name = "refresh_token", required = false) String request) {

        LoginResponse loginResponse = authService.refreshToken(request);
        String token = loginResponse.getToken();

        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setData(token);

        return ResponseEntity.ok()
                .body(apiResponse);
    }
}
