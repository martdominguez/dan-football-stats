package com.miniscore.live.event;

import java.time.Instant;

public record GoalScoredEvent(
        String matchId,
        Long leagueId,
        String leagueName,
        Long teamId,
        String teamName,
        Long playerId,
        String playerName,
        Integer minute,
        Integer homeScore,
        Integer awayScore,
        Instant occurredAt
) {
}
