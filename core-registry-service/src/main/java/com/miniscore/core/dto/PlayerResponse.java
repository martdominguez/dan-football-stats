package com.miniscore.core.dto;

import java.util.UUID;

public record PlayerResponse(
        UUID playerId,
        String firstName,
        String lastName,
        String position,
        Integer shirtNumber,
        UUID teamId,
        String teamName
) {
}
