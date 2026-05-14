package com.miniscore.live.dto;

import java.time.Instant;
import java.util.List;

public record MatchResponse(
        String matchId,
        Long leagueId,
        String leagueName,
        Long homeTeamId,
        String homeTeamName,
        Long awayTeamId,
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
