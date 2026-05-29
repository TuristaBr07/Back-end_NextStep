package com.nextstep.backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nextstep.backend.dtos.ChatRequestDTO;
import com.nextstep.backend.dtos.ChatResponseDTO;
import com.nextstep.backend.models.Usuario;
import com.nextstep.backend.services.ChatbotService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/chatbot" )
public class ChatbotController {

    private final ChatbotService chatbotService;

    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping
    public ResponseEntity<ChatResponseDTO> conversar(@Valid @RequestBody ChatRequestDTO request) {
        Usuario usuarioLogado = getUsuarioLogado();
        return ResponseEntity.ok(chatbotService.conversar(request, usuarioLogado));
    }

    private Usuario getUsuarioLogado() {
        return (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}