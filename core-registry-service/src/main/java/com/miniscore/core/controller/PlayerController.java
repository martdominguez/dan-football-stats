package com.miniscore.core.controller;

import com.miniscore.core.dto.CreatePlayerRequest;
import com.miniscore.core.dto.PlayerResponse;
import com.miniscore.core.service.PlayerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
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
@RequestMapping("/api/players")
@Tag(name = "Players", description = "Operations for managing players in the core registry.")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping
    @Operation(summary = "List players", description = "Returns all players registered in the core registry service.")
    public List<PlayerResponse> getPlayers() {
        return playerService.getAll();
    }

    @GetMapping("/{playerId}")
    @Operation(summary = "Get player by id", description = "Returns a single player using the player ID owned by the core registry service.")
    public PlayerResponse getPlayer(@PathVariable UUID playerId) {
        return playerService.getById(playerId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create player", description = "Creates a new player for an existing team.")
    public PlayerResponse createPlayer(@Valid @RequestBody CreatePlayerRequest request) {
        return playerService.create(request);
    }
}
