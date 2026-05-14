package com.miniscore.stats.client.dto;

public record CoreTeamResponse(
        Long teamId,
        String name,
        String shortName,
        Long leagueId,
        String leagueName
) {
}
