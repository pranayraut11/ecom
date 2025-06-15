package com.ecom.order.util;

import javax.servlet.http.HttpServletRequest;

public class UserContextUtil {
    public static String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    public static String extractUserId(HttpServletRequest request) {
        String token = extractToken(request);
        return JwtUtil.getUserIdFromToken(token);
    }
}
