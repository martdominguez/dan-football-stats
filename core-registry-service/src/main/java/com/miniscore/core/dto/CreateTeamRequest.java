package com.miniscore.core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateTeamRequest(
        @NotNull Long leagueId,
        @NotBlank String name,
        @NotBlank String shortName
) {
}
