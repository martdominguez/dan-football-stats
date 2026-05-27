package com.miniscore.stats.dto;

public record ObservabilityDemoResponse(
        String serviceName,
        String traceId,
        String spanId,
        Long teamId,
        String teamName,
        String teamShortName,
        Long playerId,
        String playerName,
        String note
) {
}
