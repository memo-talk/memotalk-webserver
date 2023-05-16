package com.memotalk.config.jwt;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
public class TokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-expiration-in-ms}")
    private long accessExpirationInMs;

    @Value("${jwt.refresh-expiration-in-ms}")
    private long refreshExpirationInMs;

    // 액세스 토큰을 생성합니다.
    public String generateAccessToken(String email) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + accessExpirationInMs);
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String generateRefreshToken(String email) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + refreshExpirationInMs);
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // 토큰으로 부터 이메일 추출
    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    // 토큰의 유효성  검사
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (SignatureException ex) {
            log.info("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.info("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.info("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.info("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.info("JWT claims string is empty");
        }
        return false;
    }

    public Claims getExpiredTokenClaims(String token) {
        try {
            Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token.");
            return e.getClaims();
        }
        return null;
    }

    public boolean validate(String token) {
        return this.getTokenClaims(token) != null;
    }
    public Claims getTokenClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (SecurityException e) {
            log.info("Invalid JWT signature.");
        } catch (MalformedJwtException e) {
            log.info("Invalid JWT token.");
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token.");
            return e.getClaims();
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token.");
        } catch (IllegalArgumentException e) {
            log.info("JWT token compact of handler are invalid.");
        }
        return null;
    }
}