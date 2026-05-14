package com.miniscore.live.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record CreateMatchRequest(
        @NotNull Long leagueId,
        @NotBlank String leagueName,
        @NotNull Long homeTeamId,
        @NotBlank String homeTeamName,
        @NotNull Long awayTeamId,
        @NotBlank String awayTeamName,
        Instant kickoffTime
) {
}
