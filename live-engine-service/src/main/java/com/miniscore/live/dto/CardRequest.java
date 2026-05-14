package com.miniscore.live.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CardRequest(
        @NotNull Long teamId,
        @NotNull Long playerId,
        @NotBlank String playerName,
        @NotBlank String cardType,
        @Min(0) @Max(130) Integer minute
) {
}
