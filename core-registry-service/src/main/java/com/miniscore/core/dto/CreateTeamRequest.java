package com.miniscore.core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateTeamRequest(
        UUID teamId,
        @NotNull Long leagueId,
        @NotBlank String name,
        @NotBlank String shortName
) {
}
