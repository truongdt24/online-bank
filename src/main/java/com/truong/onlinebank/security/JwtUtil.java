package com.truong.onlinebank.security;
import io.jsonwebtoken.*;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;



@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    // gen token từ số tài khoản
    public String generateToken (String cardNumber){
        return Jwts.builder()
                .subject(cardNumber)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getKey())
                .compact();
    }

    // lấy số tài khoản từ token
    public String getCardNumber(String token){
        return Jwts.parser()
                .verifyWith((javax.crypto.SecretKey) getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
    // kiểm tra token
    public boolean isValid(String token){
        try{
            Jwts.parser()
                    .verifyWith((javax.crypto.SecretKey) getKey())
                    .build()
                    .parseSignedClaims(token);
            return true;

        }catch (JwtException e){
            return false;
        }
    }
    private Key getKey(){
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
}
