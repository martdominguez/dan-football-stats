package com.miniscore.stats.client.dto;

import java.util.UUID;

public record CoreTeamResponse(
        UUID teamId,
        String name,
        String shortName,
        Long leagueId,
        String leagueName
) {
}
