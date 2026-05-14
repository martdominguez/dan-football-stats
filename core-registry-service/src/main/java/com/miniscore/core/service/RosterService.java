package com.miniscore.core.service;

import com.miniscore.core.dto.RosterResponse;
import com.miniscore.core.entity.Team;
import com.miniscore.core.exception.ResourceNotFoundException;
import com.miniscore.core.repository.PlayerRepository;
import com.miniscore.core.repository.TeamRepository;
import org.springframework.stereotype.Service;

@Service
public class RosterService {

    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final PlayerService playerService;

    public RosterService(TeamRepository teamRepository, PlayerRepository playerRepository, PlayerService playerService) {
        this.teamRepository = teamRepository;
        this.playerRepository = playerRepository;
        this.playerService = playerService;
    }

    public RosterResponse getByTeamId(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found: " + teamId));

        return new RosterResponse(
                team.getTeamId(),
                team.getName(),
                team.getLeague().getName(),
                playerRepository.findByTeamTeamIdOrderByShirtNumberAsc(teamId).stream()
                        .map(playerService::toResponse)
                        .toList()
        );
    }
}
