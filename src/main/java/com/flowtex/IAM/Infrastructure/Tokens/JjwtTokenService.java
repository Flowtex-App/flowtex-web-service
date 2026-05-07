package com.flowtex.IAM.Infrastructure.Tokens;

import com.flowtex.IAM.Application.Internal.OutboundServices.TokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Service
public class JjwtTokenService implements TokenService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JjwtTokenService.class);

    @Value("${flowtex.security.jwt.secret}")
    private String secret;

    @Value("${flowtex.security.jwt.expiration-ms}")
    private long expirationMs;

    @Value("${flowtex.security.jwt.issuer}")
    private String issuer;

    private SecretKey signingKey;

    @PostConstruct
    public void init() {
        byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(secret);
            if (keyBytes.length < 32) {
                throw new IllegalArgumentException("decoded key too short");
            }
        } catch (Exception ignored) {
            keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        }
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public String generateToken(String username) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationMs);
        return Jwts.builder()
                .setSubject(username)
                .setIssuer(issuer)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public String getUsernameFromToken(String token) {
        return resolveClaim(token, Claims::getSubject);
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            LOGGER.debug("Invalid JWT: {}", ex.getMessage());
            return false;
        }
    }

    private <T> T resolveClaim(String token, Function<Claims, T> resolver) {
        Claims claims = Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token).getBody();
        return resolver.apply(claims);
    }
}
