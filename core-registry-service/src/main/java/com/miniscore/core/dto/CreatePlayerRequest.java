package com.miniscore.core.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreatePlayerRequest(
        @NotNull UUID teamId,
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String position,
        @Min(1) @Max(99) Integer shirtNumber
) {
}
