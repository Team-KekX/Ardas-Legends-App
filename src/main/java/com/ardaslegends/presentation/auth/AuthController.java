package com.ardaslegends.presentation.auth;

import com.ardaslegends.presentation.AbstractRestController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.http.HttpClient;

@RequiredArgsConstructor
@Slf4j

@RestController
@RequestMapping(AuthController.BASE_URL)
public class AuthController extends AbstractRestController {
    public static final String BASE_URL = "/auth";
    private static final String PATH_AUTHORIZE = "/authorize";

    @GetMapping(PATH_AUTHORIZE)
    public HttpEntity<Void> authorize(String code) {
        log.debug("Incoming authorization request with code [{}]", code);

        return null;
    }
}
