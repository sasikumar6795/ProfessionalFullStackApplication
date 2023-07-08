package com.sasicode.fullStackApp.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.sql.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;


@Service
public class JWTUtil {

    private static final String SECRET_KEY = "SASICODE_1232345_FULLSTACK_DEVLOPMENT";


    public String issueToken(String subject) {
        return issueToken(subject, Map.of());
    }

    public String issueToken(String subject, String ...scopes) {
        return issueToken(subject, Map.of("scopes", scopes));
    }

    public String issueToken(String subject, Map<String , Object>  claims) {
        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuer("https://sasicode.com")
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(java.util.Date.from(Instant.now().plus(15, ChronoUnit.DAYS)))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();

        return token;
    }

    public String getSubject(String token) {
        Claims claims = getClaims(token);
        return claims.getSubject();
    }

    private Claims getClaims(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims;
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public boolean isTokenValid(String jwtToken, String username) {
        String subject = getSubject(jwtToken);
        return subject.equals(username) && !isTokenExpired(jwtToken);
    }

    private boolean isTokenExpired(String jwtToken) {
       return getClaims(jwtToken).getExpiration().before(Date.from(Instant.now()));
    }
}
