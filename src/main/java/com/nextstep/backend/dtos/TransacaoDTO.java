package com.nextstep.backend.dtos;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record TransacaoDTO(
        String date,

        @NotBlank
        @Size(max = 50)
        String type,

        @NotBlank
        @Size(max = 100)
        String category,

        @Size(max = 500)
        String description,

        @NotNull
        @DecimalMin("0.01")
        @DecimalMax("999999999.99")
        Double amount,

        @Pattern(regexp = "^(?i)(PAGO|PENDENTE)$")
        String status
) {
}
