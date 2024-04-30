package com.ardaslegends.service.auth;

import com.ardaslegends.domain.Player;
import com.ardaslegends.repository.player.PlayerRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import io.jsonwebtoken.security.SecretKeyBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Provider;
import java.util.Date;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtTokenService implements TokenService {

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Autowired
    @Qualifier("jwtKey")
    private SecretKeySpec key;

    private final PlayerRepository playerRepository;

    @Override
    public Player extractPlayer(String token) {
        Objects.requireNonNull(token, "Token must not be null");
        val claims = extractAllClaims(token);
        val discordId = claims.getSubject();

        val player = playerRepository.queryByDiscordId(discordId);
        return player;
    }

    @Override
    public String generateAuthenticationToken(Player player) {
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
    public String generateRegistrationtoken(String discordId) {
        Objects.requireNonNull(discordId, "DiscordID must not be null");
        log.debug("Building JWT Registration Token for Discord ID [{}]", discordId);

        val jwt = Jwts.builder()
                .issuer(activeProfile)
                .subject(discordId)
                .issuedAt(new Date())
                .expiration(DateUtils.addDays(new Date(), 1))
                .signWith(key)
                .compact();

        log.info("Successfully build JWT Registration Token for Discord ID [{}]", discordId);
        return jwt;
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        return false;
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token).getPayload();
    }
}