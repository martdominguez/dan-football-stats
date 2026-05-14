package com.miniscore.stats.client.dto;

public record CorePlayerResponse(
        Long playerId,
        String firstName,
        String lastName,
        String position,
        Integer shirtNumber,
        Long teamId,
        String teamName
) {
}
