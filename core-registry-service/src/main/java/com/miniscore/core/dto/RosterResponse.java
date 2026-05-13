package com.miniscore.core.dto;

import java.util.List;
import java.util.UUID;

public record RosterResponse(
        UUID teamId,
        String teamName,
        String leagueName,
        List<PlayerResponse> players
) {
}
