package com.miniscore.core.dto;

import java.util.List;

public record RosterResponse(
        Long teamId,
        String teamName,
        String leagueName,
        List<PlayerResponse> players
) {
}
