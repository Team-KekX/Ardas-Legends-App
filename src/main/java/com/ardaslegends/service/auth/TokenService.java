package com.ardaslegends.service.auth;

import com.ardaslegends.domain.Player;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface TokenService {
    String extractDiscordId(String token);
    String generateToken(Player player);
    boolean isTokenValid(String token, UserDetails userDetails);
}
