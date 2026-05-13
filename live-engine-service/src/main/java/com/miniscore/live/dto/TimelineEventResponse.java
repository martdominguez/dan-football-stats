package com.miniscore.live.dto;

import java.time.Instant;
import java.util.UUID;

public record TimelineEventResponse(
        String type,
        Integer minute,
        UUID teamId,
        String teamName,
        UUID playerId,
        String playerName,
        String cardType,
        Instant recordedAt
) {
}
