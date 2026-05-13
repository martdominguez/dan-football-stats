package com.miniscore.stats.event;

import java.time.Instant;
import java.util.UUID;

public record GoalScoredEvent(
        UUID matchId,
        UUID leagueId,
        String leagueName,
        UUID teamId,
        String teamName,
        UUID playerId,
        String playerName,
        Integer minute,
        Integer homeScore,
        Integer awayScore,
        Instant occurredAt
) {
}
