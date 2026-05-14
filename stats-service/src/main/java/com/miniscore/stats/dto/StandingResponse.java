package com.miniscore.stats.dto;

public record StandingResponse(
        Long teamId,
        String teamName,
        String teamShortName,
        String leagueName,
        Integer played,
        Integer won,
        Integer drawn,
        Integer lost,
        Integer goalsFor,
        Integer goalsAgainst,
        Integer points
) {
}
