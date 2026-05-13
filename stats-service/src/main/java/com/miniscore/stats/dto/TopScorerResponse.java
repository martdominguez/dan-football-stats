package com.miniscore.stats.dto;

import java.util.UUID;

public record TopScorerResponse(
        UUID playerId,
        String playerName,
        String position,
        Integer shirtNumber,
        UUID teamId,
        String teamName,
        String teamShortName,
        Integer goals
) {
}
