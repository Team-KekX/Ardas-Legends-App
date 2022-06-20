package com.ardaslegends.data.presentation.test;

import com.ardaslegends.data.domain.Player;
import com.ardaslegends.data.repository.RegionRepository;
import com.ardaslegends.data.repository.TestFactionRepo;
import com.ardaslegends.data.service.Pathfinder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor

@RestController
@RequestMapping("/test")
public class PathfinderBenchmark {

    private final Pathfinder pathfinder;
    private final TestFactionRepo factionRepo;

    @GetMapping("/{start}-{end}")
    public HttpEntity<Long> testPathfinder(@PathVariable String start, @PathVariable String end) {
        Player player = Player.builder().faction(factionRepo.findById("Gondor").get()).build();
        var startTime = System.nanoTime();
        pathfinder.findShortestWay(start,end, player, true);
        var endTime = System.nanoTime();
        return ResponseEntity.ok((endTime - startTime) / 1000000);
    }
    @GetMapping("/spf/{start}-{end}")
    public HttpEntity<Long> spfTestPathfinder(@PathVariable String start, @PathVariable String end) {
        Player player = Player.builder().faction(factionRepo.findById("Gondor").get()).build();
        var startTime = System.nanoTime();
        pathfinder.spfFindShortestWay(start,end, player, true);
        var endTime = System.nanoTime();
        return ResponseEntity.ok((endTime - startTime) / 1000000);
    }

}
