package com.miniscore.stats.dto;

public record TopScorerResponse(
        Long playerId,
        String playerName,
        String position,
        Integer shirtNumber,
        Long teamId,
        String teamName,
        String teamShortName,
        Integer goals
) {
}
