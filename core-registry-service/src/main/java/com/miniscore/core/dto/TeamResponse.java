package com.miniscore.core.dto;

import java.util.UUID;

public record TeamResponse(
        UUID teamId,
        String name,
        String shortName,
        Long leagueId,
        String leagueName
) {
}
