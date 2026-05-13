package com.miniscore.live.controller;

import com.miniscore.live.dto.CardRequest;
import com.miniscore.live.dto.CreateMatchRequest;
import com.miniscore.live.dto.GoalRequest;
import com.miniscore.live.dto.MatchResponse;
import com.miniscore.live.service.MatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/matches")
@Tag(name = "Matches", description = "Live match operations and event-producing actions.")
public class MatchController {

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create match", description = "Creates a new live match document in MongoDB.")
    public MatchResponse createMatch(@Valid @RequestBody CreateMatchRequest request) {
        return matchService.createMatch(request);
    }

    @GetMapping("/{matchId}")
    @Operation(summary = "Get match", description = "Returns the current live state and timeline of a match.")
    public MatchResponse getMatch(@PathVariable UUID matchId) {
        return matchService.getMatch(matchId);
    }

    @PostMapping("/{matchId}/start")
    @Operation(summary = "Start match", description = "Transitions a match from CREATED to STARTED.")
    public MatchResponse startMatch(@PathVariable UUID matchId) {
        return matchService.startMatch(matchId);
    }

    @PostMapping("/{matchId}/goals")
    @Operation(summary = "Register goal", description = "Appends a goal event to the match timeline and publishes a GoalScoredEvent.")
    public MatchResponse registerGoal(@PathVariable UUID matchId, @Valid @RequestBody GoalRequest request) {
        return matchService.registerGoal(matchId, request);
    }

    @PostMapping("/{matchId}/cards")
    @Operation(summary = "Register card", description = "Appends a card event to the match timeline.")
    public MatchResponse registerCard(@PathVariable UUID matchId, @Valid @RequestBody CardRequest request) {
        return matchService.registerCard(matchId, request);
    }

    @PostMapping("/{matchId}/end")
    @Operation(summary = "End match", description = "Transitions a match to ENDED and publishes a MatchEndedEvent.")
    public MatchResponse endMatch(@PathVariable UUID matchId) {
        return matchService.endMatch(matchId);
    }
}
