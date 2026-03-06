package com.example.orderservice.util;

import jakarta.servlet.http.HttpServletRequest;

public class RequestUtil {

    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            // Nếu có nhiều IP trong chuỗi, lấy IP đầu tiên
            return ip.split(",")[0];
        }
        return request.getRemoteAddr();
    }
}
