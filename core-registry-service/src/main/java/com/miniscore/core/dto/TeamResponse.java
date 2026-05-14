package com.miniscore.core.dto;

public record TeamResponse(
        Long teamId,
        String name,
        String shortName,
        Long leagueId,
        String leagueName
) {
}
