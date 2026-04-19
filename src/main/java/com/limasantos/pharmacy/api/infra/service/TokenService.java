package com.limasantos.pharmacy.api.infra.service;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.limasantos.pharmacy.api.user.models.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;


    public String generateToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
             String token = JWT.create()
                    .withIssuer("pharmacy-api")
                    .withSubject(user.getUsername())
                     .withExpiresAt(generateExpirationDate())
                    .sign(algorithm);
             return token;

        } catch (JWTCreationException e) {

            throw new RuntimeException("Error generating token", e);


        }


    }


    public String validateToken(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }

        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("pharmacy-api")
                    .build()
                    .verify(token)
                    .getSubject();



        } catch (JWTVerificationException e) {
            throw new RuntimeException("Invalid or expired token", e);
        }

    }


    private Instant generateExpirationDate() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.ofHours(-3));
    }
}
