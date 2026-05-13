package com.miniscore.stats.service;

import com.miniscore.stats.client.CoreRegistryClient;
import com.miniscore.stats.client.dto.CorePlayerResponse;
import com.miniscore.stats.client.dto.CoreTeamResponse;
import com.miniscore.stats.dto.StandingResponse;
import com.miniscore.stats.dto.TopScorerResponse;
import com.miniscore.stats.entity.PlayerScorer;
import com.miniscore.stats.entity.TeamStanding;
import org.springframework.stereotype.Service;

@Service
public class StatsEnrichmentService {

    private final CoreRegistryClient coreRegistryClient;

    public StatsEnrichmentService(CoreRegistryClient coreRegistryClient) {
        this.coreRegistryClient = coreRegistryClient;
    }

    public StandingResponse enrichStanding(TeamStanding row) {
        CoreTeamResponse team = coreRegistryClient.getTeam(row.getTeamId());
        return new StandingResponse(
                row.getTeamId(),
                team.name(),
                team.shortName(),
                team.leagueName(),
                row.getPlayed(),
                row.getWon(),
                row.getDrawn(),
                row.getLost(),
                row.getGoalsFor(),
                row.getGoalsAgainst(),
                row.getPoints()
        );
    }

    public TopScorerResponse enrichTopScorer(PlayerScorer row) {
        CorePlayerResponse player = coreRegistryClient.getPlayer(row.getPlayerId());
        CoreTeamResponse team = coreRegistryClient.getTeam(row.getTeamId());

        return new TopScorerResponse(
                row.getPlayerId(),
                joinName(player.firstName(), player.lastName(), row.getPlayerName()),
                player.position(),
                player.shirtNumber(),
                row.getTeamId(),
                team.name(),
                team.shortName(),
                row.getGoals()
        );
    }

    private String joinName(String firstName, String lastName, String fallback) {
        String fullName = ((firstName == null ? "" : firstName) + " " + (lastName == null ? "" : lastName)).trim();
        return fullName.isBlank() ? fallback : fullName;
    }
}
