package com.miniscore.stats.dto;

import java.util.UUID;

public record TopScorerResponse(
        UUID playerId,
        String playerName,
        UUID teamId,
        String teamName,
        Integer goals
) {
}
