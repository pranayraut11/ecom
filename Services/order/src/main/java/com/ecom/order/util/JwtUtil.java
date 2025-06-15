package com.ecom.order.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.util.Base64;

public class JwtUtil {
    // You should store your secret key securely (e.g., env variable)
    private static final String SECRET_KEY = "your-256-bit-secret"; // Replace with your actual secret

    public static String getUserIdFromToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(SECRET_KEY));
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            // Usually the userId is in the 'sub' (subject) claim
            return claims.getSubject();
        } catch (Exception e) {
            return null;
        }
    }
}
