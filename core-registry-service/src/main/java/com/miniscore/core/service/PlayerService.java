package com.miniscore.core.service;

import com.miniscore.core.dto.CreatePlayerRequest;
import com.miniscore.core.dto.PlayerResponse;
import com.miniscore.core.entity.Player;
import com.miniscore.core.entity.Team;
import com.miniscore.core.exception.ResourceNotFoundException;
import com.miniscore.core.repository.PlayerRepository;
import com.miniscore.core.repository.TeamRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;

    public PlayerService(PlayerRepository playerRepository, TeamRepository teamRepository) {
        this.playerRepository = playerRepository;
        this.teamRepository = teamRepository;
    }

    public PlayerResponse create(CreatePlayerRequest request) {
        Team team = teamRepository.findById(request.teamId())
                .orElseThrow(() -> new ResourceNotFoundException("Team not found: " + request.teamId()));

        Player player = new Player(
                UUID.randomUUID(),
                request.firstName(),
                request.lastName(),
                request.position(),
                request.shirtNumber(),
                team
        );

        return toResponse(playerRepository.save(player));
    }

    public List<PlayerResponse> getAll() {
        return playerRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    PlayerResponse toResponse(Player player) {
        return new PlayerResponse(
                player.getPlayerId(),
                player.getFirstName(),
                player.getLastName(),
                player.getPosition(),
                player.getShirtNumber(),
                player.getTeam().getTeamId(),
                player.getTeam().getName()
        );
    }
}
