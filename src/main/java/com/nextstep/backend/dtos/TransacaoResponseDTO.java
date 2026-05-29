package com.nextstep.backend.dtos;

public record TransacaoResponseDTO(
        Long id,
        String date,
        String type,
        String category,
        String description,
        Double amount,
        String status
) {
}