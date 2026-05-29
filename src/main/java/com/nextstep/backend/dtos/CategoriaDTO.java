package com.nextstep.backend.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoriaDTO(
        @NotBlank @Size(max = 100) String name,
        @NotBlank String type
) {
}
