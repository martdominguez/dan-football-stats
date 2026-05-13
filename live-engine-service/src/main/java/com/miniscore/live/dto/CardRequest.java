package com.miniscore.live.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CardRequest(
        @NotNull UUID teamId,
        @NotNull UUID playerId,
        @NotBlank String playerName,
        @NotBlank String cardType,
        @Min(0) @Max(130) Integer minute
) {
}
