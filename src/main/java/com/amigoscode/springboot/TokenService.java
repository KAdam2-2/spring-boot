package com.amigoscode.springboot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
@Slf4j
@Service
public class TokenService {

    private final JwtEncoder jwtEncoder;

    public TokenService(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }
    public String generateToken(User user) {

        Instant now = Instant.now();
        JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256)
                .build();

        String userRole = (user.getRole() != null) ? user.getRole().toUpperCase() : "VIEWER";
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(1, ChronoUnit.HOURS))
                .subject(user.getEmail())
                .claim("scope", userRole)
                .build();

        String str =  this.jwtEncoder
                .encode(JwtEncoderParameters.from(jwsHeader, claims))
                .getTokenValue();
        try{
            // Split and log each segment separately
            String[] parts = str.split("\\.");
            log.info("Header: {}", parts[0]);
            log.info("Payload: {}", parts[1]);
            log.info("Signature: {}", parts[2]);
            log.info("Payload length: {}", parts[1].length());
            java.nio.file.Files.writeString(
                    java.nio.file.Path.of("token.txt"),
                    str);
            log.info("Token is: {}", str);
        }catch(Exception ignored){

        }

        return str;
    }
}
