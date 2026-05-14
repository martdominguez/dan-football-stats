package com.miniscore.core.controller;

import com.miniscore.core.dto.CreateTeamRequest;
import com.miniscore.core.dto.TeamResponse;
import com.miniscore.core.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/teams")
@Tag(name = "Teams", description = "Operations for managing teams in the core registry.")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @GetMapping
    @Operation(summary = "List teams", description = "Returns all teams registered in the core registry service.")
    public List<TeamResponse> getTeams() {
        return teamService.getAll();
    }

    @GetMapping("/{teamId}")
    @Operation(summary = "Get team by id", description = "Returns a single team using the team ID owned by the core registry service.")
    public TeamResponse getTeam(@PathVariable Long teamId) {
        return teamService.getById(teamId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create team", description = "Creates a new team with an auto-generated team ID.")
    public TeamResponse createTeam(@Valid @RequestBody CreateTeamRequest request) {
        return teamService.create(request);
    }
}
