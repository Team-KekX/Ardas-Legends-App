package com.ardaslegends.service.auth;

import com.ardaslegends.domain.Player;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import io.jsonwebtoken.security.SecretKeyBuilder;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Provider;
import java.util.Date;
import java.util.Objects;

@Slf4j
public class JwtTokenService implements TokenService {

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Autowired
    @Qualifier("jwtKey")
    private SecretKeySpec key;

    @Override
    public String extractDiscordId(String token) {
        return null;
    }

    @Override
    public String generateToken(Player player) {
        Objects.requireNonNull(player, "Player must not be null when generating authorization credentials");
        log.debug("Building JWT Auth Token for Player [{}]", player.getIgn());

        val jwt = Jwts.builder()
                .issuer(activeProfile)
                .subject(player.getDiscordID())
                .issuedAt(new Date())
                .expiration(DateUtils.addDays(new Date(), 30))
                .signWith(key)
                .compact();

        log.info("Successfully build JWT Auth Token for Player [{}]", player.getIgn());
        return jwt;
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        return false;
    }

}