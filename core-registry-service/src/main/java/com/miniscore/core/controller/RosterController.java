package com.miniscore.core.controller;

import com.miniscore.core.dto.RosterResponse;
import com.miniscore.core.service.RosterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rosters")
@Tag(name = "Rosters", description = "Roster lookup operations in the core registry.")
public class RosterController {

    private final RosterService rosterService;

    public RosterController(RosterService rosterService) {
        this.rosterService = rosterService;
    }

    @GetMapping("/team/{teamId}")
    @Operation(summary = "Get team roster", description = "Returns the roster for a given team ID, including team and league context.")
    public RosterResponse getRoster(@PathVariable Long teamId) {
        return rosterService.getByTeamId(teamId);
    }
}
