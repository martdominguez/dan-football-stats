package com.miniscore.core.dto;

public record PlayerResponse(
        Long playerId,
        String firstName,
        String lastName,
        String position,
        Integer shirtNumber,
        Long teamId,
        String teamName
) {
}
