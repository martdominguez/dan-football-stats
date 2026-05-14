package com.miniscore.live.dto;

import java.time.Instant;

public record TimelineEventResponse(
        String type,
        Integer minute,
        Long teamId,
        String teamName,
        Long playerId,
        String playerName,
        String cardType,
        Instant recordedAt
) {
}
