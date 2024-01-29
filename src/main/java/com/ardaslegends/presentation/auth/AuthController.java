package com.ardaslegends.presentation.auth;

import com.ardaslegends.presentation.AbstractRestController;
import com.ardaslegends.presentation.discord.config.BotProperties;
import com.ardaslegends.presentation.exceptions.AuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.hc.client5.http.utils.Base64;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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


        return null;
    }

    private TokenAuthResponse getAuthToken(@NonNull String code,@NonNull String redirectUrl) {
        log.debug("Executing oauth2/token request [code: {}, redirectUrl: {}]", code, redirectUrl);
        try {
            log.trace("Building Request Body");

            MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
            requestBody.add("grant_type", "authorization_code");
            requestBody.add("redirect_uri", redirectUrl);
            requestBody.add("code", code);

            String authCreds = botProperties.getClientId() + ":" + botProperties.getClientSecret();
            byte[] authCredsBytes64 = Base64.encodeBase64(authCreds.getBytes());
            String authCreds64 = new String(authCredsBytes64);

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

}
