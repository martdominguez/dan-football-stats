package com.miniscore.stats.dto;

import java.util.UUID;

public record StandingResponse(
        UUID teamId,
        String teamName,
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
