package com.miniscore.live.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record MatchResponse(
        UUID matchId,
        UUID leagueId,
        String leagueName,
        UUID homeTeamId,
        String homeTeamName,
        UUID awayTeamId,
        String awayTeamName,
        String status,
        Integer homeScore,
        Integer awayScore,
        Instant kickoffTime,
        Instant startedAt,
        Instant endedAt,
        List<TimelineEventResponse> timeline
) {
}
