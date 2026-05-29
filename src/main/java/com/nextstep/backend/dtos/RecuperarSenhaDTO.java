package com.nextstep.backend.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RecuperarSenhaDTO(
        @NotBlank @Email String email
) {
}
