package com.miniscore.live.event;

import java.time.Instant;

public record MatchEndedEvent(
        String matchId,
        Long leagueId,
        String leagueName,
        Long homeTeamId,
        String homeTeamName,
        Integer homeScore,
        Long awayTeamId,
        String awayTeamName,
        Integer awayScore,
        Instant occurredAt
) {
}
