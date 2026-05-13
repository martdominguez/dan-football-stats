package com.miniscore.stats.event;

import java.time.Instant;
import java.util.UUID;

public record MatchEndedEvent(
        UUID matchId,
        UUID leagueId,
        String leagueName,
        UUID homeTeamId,
        String homeTeamName,
        Integer homeScore,
        UUID awayTeamId,
        String awayTeamName,
        Integer awayScore,
        Instant occurredAt
) {
}
