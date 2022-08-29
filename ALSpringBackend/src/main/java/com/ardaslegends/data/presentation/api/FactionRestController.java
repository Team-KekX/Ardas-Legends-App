package com.ardaslegends.data.presentation.api;

import com.ardaslegends.data.presentation.AbstractRestController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor

@Slf4j
@RestController
@RequestMapping(FactionRestController.BASE_URL)
public class FactionRestController extends AbstractRestController {
    public static final String BASE_URL = "/api/faction";
}
