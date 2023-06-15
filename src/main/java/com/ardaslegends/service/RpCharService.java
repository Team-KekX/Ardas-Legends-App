package com.ardaslegends.service;

import com.ardaslegends.domain.RPChar;
import com.ardaslegends.repository.rpchar.RpcharRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor

@Service
public class RpCharService extends AbstractService<RPChar, RpcharRepository>{

    private final RpcharRepository rpcharRepository;

    public Slice<RPChar> getAll(Pageable pageable) {
        log.info("Getting slice of rpchars with data [{}]", pageable);
        return rpcharRepository.queryAll(pageable);
    }


}
