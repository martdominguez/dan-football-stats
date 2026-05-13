package com.miniscore.core.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateLeagueRequest(
        @NotBlank String name,
        @NotBlank String country
) {
}
