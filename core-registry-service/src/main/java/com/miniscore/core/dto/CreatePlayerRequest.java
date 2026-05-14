package com.miniscore.core.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreatePlayerRequest(
        @NotNull Long teamId,
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String position,
        @Min(1) @Max(99) Integer shirtNumber
) {
}
