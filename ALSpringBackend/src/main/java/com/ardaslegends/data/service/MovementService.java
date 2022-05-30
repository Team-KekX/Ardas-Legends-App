package com.ardaslegends.data.service;

import com.ardaslegends.data.domain.Region;
import com.ardaslegends.data.repository.RegionRepository;
import com.ardaslegends.data.service.dto.MovementDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@RequiredArgsConstructor

@Service
@Transactional(readOnly = true)
public class MovementService {

    private final RegionRepository repository;

    public void findShortestPath(MovementDTO dto) {
        Objects.requireNonNull(dto);
    }



}
