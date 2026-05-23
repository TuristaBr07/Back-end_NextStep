package com.nextstep.backend.dtos;

import java.time.LocalDateTime;

public record ApiErroDTO(
        LocalDateTime timestamp,
        int status,
        String erro,
        String mensagem,
        String path
) {
}