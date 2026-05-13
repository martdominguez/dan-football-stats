package com.miniscore.stats.service;

import com.miniscore.stats.dto.StandingResponse;
import com.miniscore.stats.dto.TopScorerResponse;
import com.miniscore.stats.entity.TeamStanding;
import com.miniscore.stats.repository.PlayerScorerRepository;
import com.miniscore.stats.repository.TeamStandingRepository;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class StatsQueryService {

    private final TeamStandingRepository teamStandingRepository;
    private final PlayerScorerRepository playerScorerRepository;

    public StatsQueryService(TeamStandingRepository teamStandingRepository,
                             PlayerScorerRepository playerScorerRepository) {
        this.teamStandingRepository = teamStandingRepository;
        this.playerScorerRepository = playerScorerRepository;
    }

    public List<StandingResponse> getStandings(String leagueName) {
        List<TeamStanding> rows = leagueName == null || leagueName.isBlank()
                ? teamStandingRepository.findAll()
                : teamStandingRepository.findByLeagueNameOrderByPointsDesc(leagueName);

        return rows.stream()
                .sorted(Comparator
                        .comparingInt(TeamStanding::getPoints).reversed()
                        .thenComparing(Comparator.comparingInt(TeamStanding::goalDifference).reversed())
                        .thenComparing(Comparator.comparingInt(TeamStanding::getGoalsFor).reversed())
                        .thenComparing(TeamStanding::getTeamName))
                .map(row -> new StandingResponse(
                        row.getTeamId(),
                        row.getTeamName(),
                        row.getLeagueName(),
                        row.getPlayed(),
                        row.getWon(),
                        row.getDrawn(),
                        row.getLost(),
                        row.getGoalsFor(),
                        row.getGoalsAgainst(),
                        row.getPoints()
                ))
                .toList();
    }

    public List<TopScorerResponse> getTopScorers(int limit) {
        return playerScorerRepository.findAll().stream()
                .sorted(Comparator.comparing(com.miniscore.stats.entity.PlayerScorer::getGoals).reversed()
                        .thenComparing(com.miniscore.stats.entity.PlayerScorer::getPlayerName))
                .limit(Math.max(limit, 1))
                .map(row -> new TopScorerResponse(
                        row.getPlayerId(),
                        row.getPlayerName(),
                        row.getTeamId(),
                        row.getTeamName(),
                        row.getGoals()
                ))
                .toList();
    }
}
