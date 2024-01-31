package com.ardaslegends.service.time;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor

@Slf4j
@Service
public class TimeFreezeService {

    private boolean isTimeFrozen = false;

    public void freezeTime() {
        isTimeFrozen = true;
    }
    
    public boolean isTimeFrozen() {
        return isTimeFrozen;
    }
}
