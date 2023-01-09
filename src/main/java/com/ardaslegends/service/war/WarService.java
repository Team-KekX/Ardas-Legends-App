package com.ardaslegends.service.war;

import com.ardaslegends.domain.war.War;
import com.ardaslegends.repository.WarRepository;
import com.ardaslegends.service.AbstractService;
import com.ardaslegends.service.FactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Slf4j
@Service
@Transactional(readOnly = true)
public class WarService extends AbstractService<War, WarRepository> {

    private final FactionService factionService;

}
