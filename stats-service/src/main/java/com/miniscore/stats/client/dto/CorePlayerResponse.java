package com.miniscore.stats.client.dto;

import java.util.UUID;

public record CorePlayerResponse(
        UUID playerId,
        String firstName,
        String lastName,
        String position,
        Integer shirtNumber,
        UUID teamId,
        String teamName
) {
}
