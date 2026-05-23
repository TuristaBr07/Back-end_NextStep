package com.nextstep.backend.dtos;

public record PerfilResponseDTO(
        String idUsuario,
        String fullName,
        String companyName,
        String avatar
) {
}