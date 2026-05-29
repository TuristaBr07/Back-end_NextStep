package com.nextstep.backend.dtos;

import jakarta.validation.constraints.Size;

public record PerfilDTO(
        @Size(max = 120) String fullName,
        @Size(max = 60) String companyName,
        String avatar
) {
}
