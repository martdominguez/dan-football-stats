package com.miniscore.stats.controller;

import com.miniscore.stats.dto.StandingResponse;
import com.miniscore.stats.dto.TopScorerResponse;
import com.miniscore.stats.service.StatsQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stats")
@Tag(name = "Stats", description = "Read-model endpoints for standings and top scorers.")
public class StatsController {

    private final StatsQueryService statsQueryService;

    public StatsController(StatsQueryService statsQueryService) {
        this.statsQueryService = statsQueryService;
    }

    @GetMapping("/standings")
    @Operation(summary = "Get standings", description = "Returns current standings, optionally filtered by league name.")
    public List<StandingResponse> getStandings(@RequestParam(required = false) String leagueName) {
        return statsQueryService.getStandings(leagueName);
    }

    @GetMapping("/top-scorers")
    @Operation(summary = "Get top scorers", description = "Returns the current top scorers projection with a configurable limit.")
    public List<TopScorerResponse> getTopScorers(@RequestParam(defaultValue = "10") int limit) {
        return statsQueryService.getTopScorers(limit);
    }
}
