package com.miniscore.core.controller;

import com.miniscore.core.dto.CreateLeagueRequest;
import com.miniscore.core.dto.LeagueResponse;
import com.miniscore.core.service.LeagueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/leagues")
@Tag(name = "Leagues", description = "Operations for managing leagues in the core registry.")
public class LeagueController {

    private final LeagueService leagueService;

    public LeagueController(LeagueService leagueService) {
        this.leagueService = leagueService;
    }

    @GetMapping
    @Operation(summary = "List leagues", description = "Returns all leagues registered in the core registry service.")
    public List<LeagueResponse> getLeagues() {
        return leagueService.getAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create league", description = "Creates a new league in the core registry service.")
    public LeagueResponse createLeague(@Valid @RequestBody CreateLeagueRequest request) {
        return leagueService.create(request);
    }
}
