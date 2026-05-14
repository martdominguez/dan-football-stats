package com.miniscore.core.service;

import com.miniscore.core.dto.CreateTeamRequest;
import com.miniscore.core.dto.TeamResponse;
import com.miniscore.core.entity.League;
import com.miniscore.core.entity.Team;
import com.miniscore.core.exception.ResourceNotFoundException;
import com.miniscore.core.repository.LeagueRepository;
import com.miniscore.core.repository.TeamRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final LeagueRepository leagueRepository;

    public TeamService(TeamRepository teamRepository, LeagueRepository leagueRepository) {
        this.teamRepository = teamRepository;
        this.leagueRepository = leagueRepository;
    }

    public TeamResponse create(CreateTeamRequest request) {
        League league = leagueRepository.findById(request.leagueId())
                .orElseThrow(() -> new ResourceNotFoundException("League not found: " + request.leagueId()));

        Team team = new Team(
                request.name(),
                request.shortName(),
                league
        );
        return toResponse(teamRepository.save(team));
    }

    public List<TeamResponse> getAll() {
        return teamRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public TeamResponse getById(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found: " + teamId));
        return toResponse(team);
    }

    private TeamResponse toResponse(Team team) {
        return new TeamResponse(
                team.getTeamId(),
                team.getName(),
                team.getShortName(),
                team.getLeague().getId(),
                team.getLeague().getName()
        );
    }
}
