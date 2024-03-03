package com.ardaslegends.service.auth;

import com.ardaslegends.domain.Player;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import lombok.val;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Provider;
import java.util.Date;

public class JwtTokenService implements TokenService {

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Value("${jwt.signing.key}")
    private String jwtSigningKey;

    @Override
    public String extractDiscordId(String token) {
        return null;
    }

    @Override
    public String generateToken(Player player) {
        try {
            Mac g = Mac.getInstance("HmacSHA256");

            val keySpec =  new SecretKeySpec(jwtSigningKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            val key = g.init(keySpec);

            return Jwts.builder()
                    .issuer(activeProfile)
                    .subject(player.getDiscordID())
                    .issuedAt(new Date())
                    .expiration(DateUtils.addDays(new Date(), 30))
                    .signWith(
        } catch (Exception e) {
            throw new RuntimeException();
        }


    }
    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        return false;
    }

}