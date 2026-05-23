package com.nextstep.backend.services;

import com.nextstep.backend.dtos.ChatRequestDTO;
import com.nextstep.backend.dtos.ChatResponseDTO;
import com.nextstep.backend.models.Usuario;

public interface ChatbotService {
    ChatResponseDTO conversar(ChatRequestDTO request, Usuario usuario);
}