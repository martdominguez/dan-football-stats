package com.miniscore.live.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

public record CreateMatchRequest(
        @NotNull UUID leagueId,
        @NotBlank String leagueName,
        @NotNull UUID homeTeamId,
        @NotBlank String homeTeamName,
        @NotNull UUID awayTeamId,
        @NotBlank String awayTeamName,
        Instant kickoffTime
) {
}
