package com.ardaslegends.presentation.auth;

import com.ardaslegends.presentation.AbstractRestController;
import com.ardaslegends.presentation.discord.config.BotProperties;
import com.ardaslegends.presentation.exceptions.AuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.hc.client5.http.utils.Base64;
import org.springframework.http.*;
import org.springframework.lang.NonNull;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j

@RestController
@RequestMapping(AuthController.BASE_URL)
public class AuthController extends AbstractRestController {
    public static final String BASE_URL = "/auth";
    private static final String PATH_AUTHORIZE = "/authorize";

    private final RestClient restClient;
    private final BotProperties botProperties;

    @GetMapping(PATH_AUTHORIZE)
    public HttpEntity<Void> authorize(String code, String redirectUrl) {
        log.debug("Incoming authorization request with  [code: {}, redirectUrl: {}]", code, redirectUrl);

        Objects.requireNonNull(code, "Discord auth code must not be null!");
        Objects.requireNonNull(redirectUrl, "Discord redirectUrl must not be null!");

        if (redirectUrl.equals("undefined")) throw new IllegalArgumentException("RedirectUrl must not be undefined");

        val authTokenResponse = getAuthToken(code, redirectUrl);

        // TODO: Parallelize once StructuredConcurrency drops (Java 23)
        val identityResponse = getUserIdentity(authTokenResponse);
        val guildsResponse = getGuild(authTokenResponse.tokenType(), authTokenResponse.accessToken());

        return null;
    }

    /**
     * Fetches the corresponding user with the provided authToken from discord
     * @param authTokenResponse required authToken for authorization with discord
     * @return UserIdentity object containing discord id and  username
     */
    private UserIdentityResponse getUserIdentity(TokenAuthResponse authTokenResponse) {
        Objects.requireNonNull(authTokenResponse, "AuthToken must not be null!");
        log.debug("Fetching the user identity from discord");
        try {
            val response = restClient.get()
                    .uri("https://discord.com/api/users/@me")
                    .header("Authorization", "" + authTokenResponse.tokenType() + " " + authTokenResponse.accessToken())
                    .retrieve()
                    .toEntity(UserIdentityResponse.class);


            log.debug("UserIdentity request was successful, response [{}]", response);
            return response.getBody();
        } catch (RestClientException rcException) {
            // TODO: Check if a better error message is need
            throw new AuthException(rcException.getMessage(), rcException);
        }
    }

    /**
     * Fetches the corresponding guild object that is tied to the user if the user is part of the guild
     * Optional is empty when the user is not part of the guild
     * @param tokenType
     * @param accessToken
     * @return Optional GuildsResponse, since the user may not have joined the host guild
     */
    private Optional<GuildsResponse> getGuild(String tokenType, String accessToken) {
        Objects.requireNonNull(tokenType, "TokenType must not be null!");
        Objects.requireNonNull(accessToken, "AccessToken must not be null!");

        log.debug("Fetching the user's guilds");
        try {
            val response = restClient.get()
                    .uri("https://discord.com/api/users/@me/guilds")
                    .header("Authorization", "" + tokenType + " " + accessToken)
                    .retrieve()
                    .toEntity(GuildsResponse[].class);

            val guilds = response.getBody();
            log.debug("Guilds Request was successful, guild count: [{}]", guilds.length);

            log.trace("Filtering for the Host Server Guild");

            val hostGuild = Arrays.stream(guilds)
                    .filter(guild -> guild.id().equals(botProperties.getServerId()))
                    .findAny();


            hostGuild.ifPresentOrElse(
                    guild -> log.trace("User is part of the Server [{}]", guild),
                    () -> log.trace("User is not part of the Host Server"));

            return hostGuild;
        } catch (RestClientException rcException) {
            // TODO: Check if a better error message is need
            throw new AuthException(rcException.getMessage(), rcException);
        }
    }

    private TokenAuthResponse getAuthToken(@NonNull String code,@NonNull String redirectUrl) {
        log.debug("Executing oauth2/token request [code: {}, redirectUrl: {}]", code, redirectUrl);
        try {
            log.trace("Building Request Body");

            MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
            requestBody.add("grant_type", "authorization_code");
            requestBody.add("redirect_uri", redirectUrl);
            requestBody.add("code", code);

            // Authentication Credentials need to be encoded in this format
            String authCreds64 = encodeBase64(botProperties.getClientId() + ":" + botProperties.getClientSecret());

            val response = restClient.post()
                    .uri("https://discord.com/api/oauth2/token")
                    .header("Authorization", "Basic " + authCreds64)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(requestBody)
                    .retrieve()
                    .toEntity(TokenAuthResponse.class);

            log.debug("Request was successful, response [{}]", response);
            return response.getBody();
        } catch (RestClientException rcException) {
            // TODO: Check if a better error message is need
            throw new AuthException(rcException.getMessage(), rcException);
        }
    }

    private String encodeBase64(String toEncode) {
        byte[] base64EncodedInBytes = Base64.encodeBase64(toEncode.getBytes());
        String base64String = new String(base64EncodedInBytes);
        return base64String;
    }
}
